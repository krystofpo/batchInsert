package cz.krystofample.demolibrary;

import cz.krystofample.demolibrary.entities.Book;
import cz.krystofample.demolibrary.entities.User;
import cz.krystofample.demolibrary.repositories.BookRepo;
import cz.krystofample.demolibrary.repositories.LoanRepo;
import cz.krystofample.demolibrary.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarySystem {


    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    LoanRepo loanRepo;


    public void borrowBooks(List<Book> books, User user) {
        //TODO
    }

    public void returnBooks(List<Book> books) {
        //TODO
    }
}
