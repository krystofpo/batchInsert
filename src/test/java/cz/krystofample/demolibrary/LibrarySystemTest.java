package cz.krystofample.demolibrary;

import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.Loan;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.repositories.BookRepo;
import cz.krystofample.demolibrary.repositories.LoanRepo;
import cz.krystofample.demolibrary.repositories.UserRepo;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    public void borrowBooks() {
        // -------------------------
        //given

        bookRepo.deleteAll();
        userRepo.deleteAll();
        loanRepo.deleteAll();

        Book book1 = testUtil.persistAndReturnNewBook();
        Book book2 = testUtil.persistAndReturnNewBook();


        User user = testUtil.persistAndReturnNewUser();

        librarySystem.borrowBooks(Arrays.asList(book1, book2), user);

        assertTrue(user.getLoans().size() == 1);

        Loan loan = user.getLoans().get(0);

        assertTrue(loan.getUser().equals(user));

        assertTrue(book1.getLoans().get(0).equals(loan));
        assertTrue(book2.getLoans().get(0).equals(loan));

        assertTrue(loanRepo.count() == 1L);

        Book book3 = testUtil.persistAndReturnNewBook();
        Book book4 = testUtil.persistAndReturnNewBook();

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

        assertTrue(book3.getLoans().get(0).equals(loan2));
        assertTrue(user.hasLoans());

    }

    @Test
    @Transactional
    public void returnBooks() {

        // given
        bookRepo.deleteAll();
        userRepo.deleteAll();
        loanRepo.deleteAll();

        Book book1 = testUtil.persistAndReturnNewBook();
        Book book2 = testUtil.persistAndReturnNewBook();


        User user = testUtil.persistAndReturnNewUser();

        librarySystem.borrowBooks(Arrays.asList(book1, book2), user);

        user = testUtil.reloadUser(user);

        Loan loan = user.getLoans().get(0);
//------------------------------------------

        Book book3 = testUtil.persistAndReturnNewBook();
        Book book4 = testUtil.persistAndReturnNewBook();


        librarySystem.borrowBooks(Arrays.asList(book3, book4), user);


        //---------------- when


        librarySystem.returnBooks(Arrays.asList(book1));


        // ----------then


        book1 = testUtil.reloadBook(book1);
        assertTrue(book1.getLoans().size() == 0);


        assertTrue(loan.getUser().equals(user));
        assertTrue(user.getLoans().contains(loan));
        assertTrue(loanRepo.count() == 2L);

        //======== second return

        librarySystem.returnBooks(Arrays.asList(book2));

        //==========then


        book2 = testUtil.reloadBook(book2);
        assertTrue(book2.getLoans().size() == 0);

        assertTrue(loanRepo.count() == 1L);

        //------------ thjird return

        librarySystem.returnBooks(Arrays.asList(book3, book4));

        //--------------then

        assertTrue(loanRepo.count() == 0L);
        user = testUtil.reloadUser(user);
        assertTrue(user.hasLoans() == false);
        assertTrue(user.getLoans().size() == 0);



    }


}