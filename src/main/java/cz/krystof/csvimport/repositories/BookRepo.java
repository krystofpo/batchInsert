package cz.krystof.csvimport.repositories;

import cz.krystof.csvimport.entities.CsvData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends CrudRepository<CsvData, Long> {
}
