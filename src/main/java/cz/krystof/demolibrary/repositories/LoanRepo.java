package cz.krystof.demolibrary.repositories;

import cz.krystof.demolibrary.entities.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepo extends CrudRepository<Loan, Long> {
}
