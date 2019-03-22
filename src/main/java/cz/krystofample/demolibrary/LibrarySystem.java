package cz.krystofample.demolibrary;

import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.Loan;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.repositories.BookRepo;
import cz.krystofample.demolibrary.repositories.LoanRepo;
import cz.krystofample.demolibrary.repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LibrarySystem {


    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    LoanRepo loanRepo;


    private User persistedUser;

    private List<Book> persistedBooks = new ArrayList<>();

    private Loan persistedLoan;

    private Logger log = LoggerFactory.getLogger(LibrarySystem.class);

    @Transactional
    public void borrowBooks(List<Book> books, User user) {
        validateParametersAndLoadUserAndBooks(books, user);

        Loan loan = new Loan();
        loan = loanRepo.save(loan);

        assignLoanToBooks(loan);
        assignBooksToLoan(loan);
        assignUserToLoan(loan);
        assignLoanToUser(loan);
        log.info("User with id " + user.getId() + " borrowed " + books.size() + " books.");

    }

    @Transactional
    public void returnBook(Book book) {
        validateAndLoadBooks(Arrays.asList(book));
        loadLoan();
        loadUserFromLoan();
        removeLoanFromBook();
        removeBookFromLoan();
        ifLoanIsEmptyRemoveFromUserAndDeleteIt();
        ifUserHasNoLoansChangeStatus();
        log.info("User with id " + persistedUser.getId() + " returned book with id " + book.getId());

    }

    private void loadUserFromLoan() {
        persistedUser = null;
        persistedUser = persistedLoan.getUser();
        if (persistedUser == null) {
            logErrorAndThrow("loan with id " + persistedLoan.getId() + " has no user");
        }
    }

    private void assignBooksToLoan(Loan loan) {
        loan.getBooks().addAll(persistedBooks);
    }


    private void assignLoanToBooks(Loan loan) {
        for (Book book : persistedBooks) {
            assignLoanToOneBook(book, loan);
        }
    }

    private void assignLoanToOneBook(Book book, Loan loan) {
        book.setLoan(loan);
    }

    private void assignUserToLoan(Loan loan) {
        loan.setUser(persistedUser);
    }

    private void assignLoanToUser(Loan loan) {
        persistedUser.getLoans().add(loan);
        persistedUser.setHasLoans(true);
    }

    private void validateParametersAndLoadUserAndBooks(List<Book> books, User user) {
        validateAndLoadUser(user);
        validateAndLoadBooks(books);
    }

    private void validateAndLoadBooks(List<Book> books) {
        this.persistedBooks.clear();
        for (Book book : books) {
            validateAndLoadBook(book);
        }
    }

    private void validateAndLoadUser(User user) {
        this.persistedUser = null;

        if (user == null) {
            logErrorAndThrow("user cannot be null");
        }
        if (user.getId() == null) {
            logErrorAndThrow("user id cannot be null");
        }

        Optional<User> persistedUser = userRepo.findById(user.getId());
        if (!persistedUser.isPresent()) {
            logErrorAndThrow("user with id " + user.getId() + " does not exist in DB");
        }
        this.persistedUser = persistedUser.get();
    }

    private void logErrorAndThrow(String errorMessage) {
        log.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    private void validateAndLoadBook(Book book) {
        if (book == null) {
            logErrorAndThrow("book cannot be null");
        }

        if (book.getId() == null) {
            logErrorAndThrow("book id cannot be null");
        }

        Optional<Book> persistedBook = bookRepo.findById(book.getId());
        if (!persistedBook.isPresent()) {
            logErrorAndThrow("book with id " + book.getId() + " does not exist in DB");
        }
        this.persistedBooks.add(persistedBook.get());
    }

    private void ifUserHasNoLoansChangeStatus() {
        if (persistedUser.getLoans().size() == 0) {
            persistedUser.setHasLoans(false);
        }
    }

    private void ifLoanIsEmptyRemoveFromUserAndDeleteIt() {
        if (persistedLoan.getBooks().size() == 0) {
            persistedLoan.setUser(null);
            persistedUser.getLoans().remove(persistedLoan);
            loanRepo.delete(persistedLoan);
        }
    }

    private void removeBookFromLoan() {
        persistedLoan.getBooks().remove(persistedBooks.get(0));
    }

    private void removeLoanFromBook() {
        persistedBooks.get(0).setLoan(null);
    }

    private void loadLoan() {
        persistedLoan = null;
        persistedLoan = persistedBooks.get(0).getLoan();
        if (persistedLoan == null) {
            logErrorAndThrow("book id " + persistedBooks.get(0).getId() + " is not part of any loan");
        }
    }
}
