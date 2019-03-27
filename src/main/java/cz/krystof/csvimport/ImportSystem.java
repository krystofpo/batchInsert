package cz.krystof.csvimport;

import au.com.anthonybruno.Gen;
import com.github.javafaker.Faker;
import cz.krystof.csvimport.repositories.CsvDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
@Transactional
public class ImportSystem {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CsvDataRepo csvDataRepo;


    private Logger log = LoggerFactory.getLogger(ImportSystem.class);


    public ResponseEntity csvImport() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("started to write csv file");

        generateCsvFile();
        stopWatch.stop();
        log.info("csv file took " + stopWatch.getLastTaskTimeMillis() + " ms");

//        stopWatch.start();
//        loadCsvData();
//stopWatch.stop();
//        log.info("loading csv file took " +stopWatch.getLastTaskTimeMillis() + " ms");

        stopWatch.start();
        deleteExistingData();
        stopWatch.stop();
        log.info("deleting old data took " + stopWatch.getLastTaskTimeMillis() + " ms");

        stopWatch.start();
        putDataToTable();
        stopWatch.stop();
        log.info("inserting to DB took " + stopWatch.getLastTaskTimeMillis() + " ms.");

        return new ResponseEntity(HttpStatus.OK);

    }

    private void putDataToTable() {


    }

    private void deleteExistingData() {
        csvDataRepo.deleteAll();
    }

    private void loadCsvData() {
    }

    private void generateCsvFile() {

        Faker faker = Faker.instance();

        Gen.start()
                .addField("First Name", () -> faker.name().firstName())
                .addField("Last Name", () -> faker.name().lastName())
                .generate(5000000)
                .asCsv()
                .toFile("people.csv");


    }
}
