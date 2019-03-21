package cz.krystofample.demolibrary;

import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.Loan;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.repositories.BookRepo;
import cz.krystofample.demolibrary.repositories.LoanRepo;
import cz.krystofample.demolibrary.repositories.UserRepo;
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

    @Transactional
    public void borrowBooks(List<Book> books, User user) {
        validateParametersAndLoadUserAndBooks(books, user);


        //TODO
        Loan loan = new Loan();
        loan = loanRepo.save(loan);

        assignLoanToBooks(loan);
        assignBooksToLoan(loan);
        assignUserToLoan(loan);
        assignLoanToUser(loan);


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
        if (user.getId() == null) {
            throw new IllegalArgumentException("user id cannot be null");
        }

        Optional<User> persistedUser = userRepo.findById(user.getId());
        if (!persistedUser.isPresent()) {
            throw new IllegalArgumentException("user with id " + user.getId() + " does not exist in DB");
        }
        this.persistedUser = persistedUser.get();
    }

    private void validateAndLoadBook(Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("book id cannot be null");
        }

        Optional<Book> persistedBook = bookRepo.findById(book.getId());
        if (!persistedBook.isPresent()) {
            throw new IllegalArgumentException("book with id " + book.getId() + " does not exist in DB");
        }
        this.persistedBooks.add(persistedBook.get());
    }

    @Transactional
    public void returnBook(Book book) {
        validateAndLoadBooks(Arrays.asList(book));
        loadLoan();
        persistedUser = null;
        persistedUser = persistedLoan.getUser();
        removeLoanFromBook();
        removeBookFromLoan();
        ifLoanIsEmptyRemoveFromUserAndDeleteIt();
        ifUserHasNoLoansChangeStatus();


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
    }
}
