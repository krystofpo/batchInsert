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

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemolibraryApplication.class)
public class LibrarySystemTest {

    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    LoanRepo loanRepo;

    @Autowired
    LibrarySystem librarySystem;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void borrowBooks() {

        Book book1 = new Book();
        Book book2 = new Book();

        bookRepo.deleteAll();
        userRepo.deleteAll();
        loanRepo.deleteAll();

        bookRepo.save(book2);
        bookRepo.save(book2);

        User user = new User();
        userRepo.save(user);

        librarySystem.borrowBooks(Arrays.asList(book1, book2), user);

        assertTrue(user.getLoans().size() == 1);

        Loan loan = user.getLoans().get(0);

        assertTrue(loan.getUser().equals(user));

        assertTrue(book1.getLoans().get(0).equals(loan));
        assertTrue(book2.getLoans().get(0).equals(loan));

        assertTrue(loanRepo.count() == 1L);

//------------------------------------------

        Book book3 = new Book();
        Book book4 = new Book();

        bookRepo.save(book3);
        bookRepo.save(book4);

        librarySystem.borrowBooks(Arrays.asList(book3, book4), user);

        assertTrue(user.getLoans().size() == 2);
        assertTrue(loanRepo.count() == 2L);

        Loan loan2 = user.getLoans().stream().filter(L -> !L.equals(loan)).collect(Collectors.toList()).get(0);
        assertTrue(loan2.getUser().equals(user));

        assertTrue(book3.getLoans().get(0).equals(loan2));
        assertTrue(user.hasLoans());

    }

    @Test
    public void returnBooks() {
    }
}