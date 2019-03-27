package cz.krystof.csvimport;

import au.com.anthonybruno.Gen;
import com.github.javafaker.Faker;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
@Transactional
public class ImportSystem {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CsvDataRepo csvDataRepo;

    @Autowired
    DataSource dataSource;

    private Logger log = LoggerFactory.getLogger(ImportSystem.class);


    public ResponseEntity csvImport() throws Exception {
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

        log.info("started to insert to DB using prepared statement batch");
        stopWatch.start();
        putDataToTablePreparedStatement();
        stopWatch.stop();
        log.info("inserting to DB using prepared statement took " + stopWatch.getLastTaskTimeMillis() + " ms.");

        return new ResponseEntity(HttpStatus.OK);

    }

    private void putDataToTablePreparedStatement() throws Exception {
        Connection connection = dataSource.getConnection();

// 50 000 000 records to DB. File has 5 000 000 records, reads 10 times
        for (int i = 1; i < 2; i++) { //TODO change  to i<11
            loadCsvFileAndPutToDbPreparedStatement(connection);

        }


    }

    private void loadCsvFileAndPutToDbPreparedStatement(Connection connection) throws Exception {

        CSVParser parser =
                new CSVParserBuilder()
                        .withSeparator(',')
                        .withIgnoreQuotations(true)
                        .build();
        try (
                CSVReader reader =
                        new CSVReaderBuilder(new FileReader("people.csv"))
                                .withSkipLines(1)
                                .withCSVParser(parser)
                                .build();) {
            String insertQuery = "Insert into Csvdata (firstName, lastName) values (?,?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData = null;
            int row = 0;
            while ((rowData = reader.readNext()) != null) {
                addRowToStatement(pstmt, rowData);

                if (row % 100 == 0) {// insert when the batch size is 10
                    pstmt.executeBatch();
                }
                row++;
            }
        } catch (Exception e) {
            log.error("error reading csv file", e);
            throw new Exception(e);
        }

    }

    private void addRowToStatement(PreparedStatement pstmt, String[] rowData) throws SQLException {
        int column = 1;
        for (String data : rowData) {
            pstmt.setString(column, data);


            column++;
        }
        pstmt.addBatch();
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
                .generate(50000)//TODO should be 5 000 000
                .asCsv()
                .toFile("people.csv");


    }
}
