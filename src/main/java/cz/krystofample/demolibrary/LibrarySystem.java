package cz.krystofample.demolibrary;

import com.querydsl.jpa.impl.JPAQueryFactory;
import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.Loan;
import cz.krystofample.demolibrary.entities.QBook;
import cz.krystofample.demolibrary.entities.QLoan;
import cz.krystofample.demolibrary.entities.QUser;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.web.BookRequest;
import cz.krystofample.demolibrary.web.UserAndBooksRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LibrarySystem {


    @PersistenceContext
    EntityManager em;

    private QBook book = QBook.book;
    private QUser user = QUser.user;
    private QLoan loan = QLoan.loan;

    private JPAQueryFactory query;

    private User persistedUser;

    private List<Book> persistedBooks = new ArrayList<>();

    private Loan persistedLoan;

    private Logger log = LoggerFactory.getLogger(LibrarySystem.class);

    @Transactional
    public User borrowBooks(List<Book> books, User user) {
        initJPAQuery();
        validateParametersAndLoadUserAndBooks(books, user);

        Loan loan = new Loan();
        em.persist(loan);

        assignBooksToLoan(loan);
        assignLoanToBooks(loan);
        assignUserToLoan(loan);
        assignLoanToUser(loan);
        em.flush();
        log.info("User with id " + user.getId() + " borrowed " + books.size() + " books.");
        return persistedUser;
    }

    private void initJPAQuery() {
        if (query == null) {
            query = new JPAQueryFactory(em);
        }
    }

    @Transactional
    public User returnBook(Book book) {
        initJPAQuery();
        validateAndLoadBooks(Arrays.asList(book));
        loadLoan();
        loadUserFromLoan();
        removeLoanFromBook();
        removeBookFromLoan();
        ifLoanIsEmptyRemoveFromUserAndDeleteIt();
        ifUserHasNoLoansChangeStatus();
        em.flush();
        log.info("User with id " + persistedUser.getId() + " returned book with id " + book.getId());

        return persistedUser;
    }

    @Transactional
    public User borrowBooks(UserAndBooksRequest userAndBooksRequest) {
        User user = convertUserFromRequest(userAndBooksRequest);
        List<Book> books = convertBooksFromRequest(userAndBooksRequest);
        return borrowBooks(books, user);
    }

    @Transactional
    public User returnBook(BookRequest bookRequest) {
        Book book = convertBookFromRequest(bookRequest);
        return returnBook(book);
    }

    private Book convertBookFromRequest(BookRequest bookRequest) {
        Book book = new Book();
        book.setId(bookRequest.getId());
        return book;
    }

    private List<Book> convertBooksFromRequest(UserAndBooksRequest userAndBooksRequest) {
        List<Book> books = new ArrayList<>();
        for (BookRequest bookRequest : userAndBooksRequest.getBookRequests()) {
            addToBooks(books, bookRequest);
        }
        return books;
    }

    private void addToBooks(List<Book> books, BookRequest bookRequest) {
        Book book = new Book();
        book.setId(bookRequest.getId());
        books.add(book);
    }

    private User convertUserFromRequest(UserAndBooksRequest userAndBooksRequest) {
        User user = new User();
        user.setId(userAndBooksRequest.getUserRequest().getId());
        return user;
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
        em.persist(loan);
    }


    private void assignLoanToBooks(Loan loan) {
        for (Book book : persistedBooks) {
            assignLoanToOneBook(book, loan);
        }
    }

    private void assignLoanToOneBook(Book book, Loan loan) {
        book.setLoan(loan);
        em.persist(book);
    }

    private void assignUserToLoan(Loan loan) {
        loan.setUser(persistedUser);
        em.persist(loan);
    }

    private void assignLoanToUser(Loan loan) {
        persistedUser.getLoans().add(loan);
        persistedUser.setHasLoans(true);
        em.persist(persistedUser);
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


        persistedUser = query.selectFrom(this.user).where(this.user.id.eq(user.getId())).fetchOne();
        if (persistedUser == null) {
            logErrorAndThrow("user with id " + user.getId() + " does not exist in DB");
        }
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

        Book persistedBook = query.selectFrom(this.book).where(this.book.id.eq(book.getId())).fetchOne();
        if (persistedBook == null) {
            logErrorAndThrow("book with id " + book.getId() + " does not exist in DB");
        }
        this.persistedBooks.add(persistedBook);
    }

    private void ifUserHasNoLoansChangeStatus() {
        if (persistedUser.getLoans().size() == 0) {
            persistedUser.setHasLoans(false);
            em.persist(persistedUser);
        }
    }

    private void ifLoanIsEmptyRemoveFromUserAndDeleteIt() {
        if (persistedLoan.getBooks().size() == 0) {
            persistedLoan.setUser(null);
            persistedUser.getLoans().remove(persistedLoan);
            em.persist(persistedUser);
            em.remove(persistedLoan);
        }
    }

    private void removeBookFromLoan() {
        persistedLoan.getBooks().remove(persistedBooks.get(0));
        em.persist(persistedLoan);
    }

    private void removeLoanFromBook() {
        persistedBooks.get(0).setLoan(null);
        em.persist(persistedBooks.get(0));
    }

    private void loadLoan() {
        persistedLoan = null;
        persistedLoan = persistedBooks.get(0).getLoan();
        if (persistedLoan == null) {
            logErrorAndThrow("book id " + persistedBooks.get(0).getId() + " is not part of any loan");
        }
    }
}
