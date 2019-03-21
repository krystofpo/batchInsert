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

    @Transactional
    public void borrowBooks(List<Book> books, User user) {
        validateParametersAndLoadUserAndBooks(books, user);


        //TODO
        Loan loan = new Loan();
        loanRepo.save(loan);

        assignLoanToBooks(loan);
        assignUserToLoan(loan);
        assignLoanToUser(loan);


    }


    private void assignLoanToBooks(Loan loan) {
        for (Book book : persistedBooks) {
            assignLoanToOneBook(book, loan);
        }
    }

    private void assignLoanToOneBook(Book book, Loan loan) {
        book.getLoans().add(loan);
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

    public void returnBooks(List<Book> books) {
        //TODO
    }
}
