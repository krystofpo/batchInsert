package cz.krystof.demolibrary;

import cz.krystof.demolibrary.entities.Book;
import cz.krystof.demolibrary.entities.Loan;
import cz.krystof.demolibrary.entities.User;
import cz.krystof.demolibrary.repositories.BookRepo;
import cz.krystof.demolibrary.repositories.LoanRepo;
import cz.krystof.demolibrary.repositories.UserRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
public class LibrarySystemTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    LoanRepo loanRepo;

    @Autowired
    LibrarySystem librarySystem;

    @Autowired
    TestUtil testUtil;

    @Test
    @Transactional
    public void borrowBooks() {
        // -------------------------
        //given

        bookRepo.deleteAll();
        userRepo.deleteAll();
        loanRepo.deleteAll();

        Book book1 = testUtil.persistAndGetNewBook();
        Book book2 = testUtil.persistAndGetNewBook();

        User user = testUtil.persistAndGetNewUser();


        // when

        librarySystem.borrowBooks(Arrays.asList(book1, book2), user);

        //then
        user = testUtil.reloadUser(user);

        assertTrue(user.getLoans().size() == 1);

        Loan loan = user.getLoans().get(0);

        assertTrue(loan.getUser().equals(user));

        assertTrue(book1.getLoan().equals(loan));
        assertTrue(book2.getLoan().equals(loan));

        assertTrue(loan.getBooks().size() == 2);
        assertTrue(loan.getBooks().contains(book1));
        assertTrue(loan.getBooks().contains(book2));

        assertTrue(loanRepo.count() == 1L);


        //given

        Book book3 = testUtil.persistAndGetNewBook();
        Book book4 = testUtil.persistAndGetNewBook();

//------------------------------------------
        //when


        librarySystem.borrowBooks(Arrays.asList(book3, book4), user);

        /////////----------------------------
        //-------------- then

        user = testUtil.reloadUser(user);

        assertTrue(user.getLoans().size() == 2);
        assertTrue(loanRepo.count() == 2L);

        Loan loan2 = user.getLoans().stream().filter(L -> !L.equals(loan)).collect(Collectors.toList()).get(0);
        assertTrue(loan2.getUser().equals(user));

        assertTrue(book3.getLoan().equals(loan2));
        assertTrue(loan2.getBooks().size() == 2);
        assertTrue(loan2.getBooks().contains(book3));
        assertTrue(loan2.getBooks().contains(book4));

        assertTrue(user.hasLoans());

    }

    @Test
    @Transactional
    public void returnBook() {

        // given
        bookRepo.deleteAll();
        userRepo.deleteAll();
        loanRepo.deleteAll();

        Book book1 = testUtil.persistAndGetNewBook();
        Book book2 = testUtil.persistAndGetNewBook();
        Book book3 = testUtil.persistAndGetNewBook();
        Book book4 = testUtil.persistAndGetNewBook();


        User user = testUtil.persistAndGetNewUser();

        librarySystem.borrowBooks(Arrays.asList(book1, book2), user);

        user = testUtil.reloadUser(user);
        Loan loan1 = user.getLoans().get(0);

        librarySystem.borrowBooks(Arrays.asList(book3, book4), user);


        //---------------- when


        librarySystem.returnBook(book1);


        // ----------then

        user = testUtil.reloadUser(user);
        book1 = testUtil.reloadBook(book1);
        loan1 = testUtil.reloadLoan(loan1);

        assertTrue(book1.getLoan() == null);

        assertTrue(user.getLoans().contains(loan1));
        assertTrue(user.getLoans().size() == 2);
        assertTrue(loanRepo.count() == 2L);

        assertTrue(loan1.getBooks().size() == 1);
        assertTrue(loan1.getBooks().contains(book2));
        assertTrue(loan1.getUser().equals(user));

        //======== second return, loan1 deleted, loan2 exists

        librarySystem.returnBook(book2);

        //==========then

        user = testUtil.reloadUser(user);
        book2 = testUtil.reloadBook(book2);
        Loan loan2 = loanRepo.findAll().iterator().next();

        assertTrue(book2.getLoan() == null);


        assertTrue(loanRepo.count() == 1L);
        assertTrue(user.getLoans().size() == 1);
        assertTrue(user.getLoans().contains(loan2));

        //------------  return, no books are on loan

        librarySystem.returnBook(book3);
        librarySystem.returnBook(book4);

        //--------------then

        assertTrue(loanRepo.count() == 0L);
        user = testUtil.reloadUser(user);
        assertTrue(user.hasLoans() == false);
        assertTrue(user.getLoans().size() == 0);



    }


}