package cz.krystofample.demolibrary.repositories;

import cz.krystofample.demolibrary.entities.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepo extends CrudRepository<Loan, Long> {
}
