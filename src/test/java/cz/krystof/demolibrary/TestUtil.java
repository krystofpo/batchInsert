package cz.krystof.demolibrary;

import cz.krystof.demolibrary.entities.Book;
import cz.krystof.demolibrary.entities.Loan;
import cz.krystof.demolibrary.entities.User;
import cz.krystof.demolibrary.repositories.BookRepo;
import cz.krystof.demolibrary.repositories.LoanRepo;
import cz.krystof.demolibrary.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class TestUtil {

    @Autowired
    private BookRepo bookRepo;


    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LoanRepo loanRepo;

    @Transactional
    public Book persistAndGetNewBook() {
        Book book = new Book();
        return bookRepo.save(book);
    }

    @Transactional
    public User persistAndGetNewUser() {
        User user = new User();
        return userRepo.save(user);
    }

    @Transactional
    public User reloadUser(User user) {
        return userRepo.findById(user.getId()).get();
    }

    @Transactional
    public Book reloadBook(Book book) {
        return bookRepo.findById(book.getId()).get();
    }


    @Transactional
    public Loan reloadLoan(Loan loan) {
        return loanRepo.findById(loan.getId()).get();
    }

}
