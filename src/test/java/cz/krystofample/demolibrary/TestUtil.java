package cz.krystofample.demolibrary;

import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.repositories.BookRepo;
import cz.krystofample.demolibrary.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
public class TestUtil {

    @Autowired
    private BookRepo bookRepo;


    @Autowired
    private UserRepo userRepo;

    @Transactional
    public Book persistAndReturnNewBook() {
        Book book = new Book();
        return bookRepo.save(book);
    }

    @Transactional
    public User persistAndReturnNewUser() {
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

}
