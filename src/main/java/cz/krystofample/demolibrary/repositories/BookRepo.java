package cz.krystofample.demolibrary.repositories;

import cz.krystofample.demolibrary.entities.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends CrudRepository<Book, Long> {
}
