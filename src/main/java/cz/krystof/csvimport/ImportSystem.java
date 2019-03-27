package cz.krystof.csvimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
@Transactional
public class ImportSystem {

    @PersistenceContext
    EntityManager em;


    private Logger log = LoggerFactory.getLogger(ImportSystem.class);


    public ResponseEntity csvImport() {


        return new ResponseEntity(HttpStatus.OK);

    }
}
