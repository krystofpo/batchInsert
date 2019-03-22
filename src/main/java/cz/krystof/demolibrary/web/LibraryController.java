package cz.krystof.demolibrary.web;


import cz.krystof.demolibrary.LibrarySystem;
import cz.krystof.demolibrary.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LibraryController {

    @Autowired
    LibrarySystem librarySystem;

    @PostMapping("/borrow")
    @ResponseBody
    public User borrowBooks(
            @RequestBody UserAndBooksRequest userAndBooksRequest) {
        return librarySystem.borrowBooks(userAndBooksRequest);
    }

    @PostMapping("/return")
    @ResponseBody
    public User returnBook(
            @RequestBody BookRequest bookRequest) {
        return librarySystem.returnBook(bookRequest);
    }
}
