package cz.krystof.csvimport;

import au.com.anthonybruno.Gen;
import com.github.javafaker.Faker;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import cz.krystof.csvimport.entities.Csvdata;
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
public class ImportSystem {

    @PersistenceContext
    EntityManager em;

    @Autowired
    CsvDataRepo csvDataRepo;

    @Autowired
    DataSource dataSource;

    private Long idCounter;

    private Logger log = LoggerFactory.getLogger(ImportSystem.class);


    public ResponseEntity csvImport() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("started to write csv file");

        generateCsvFile();

        stopWatch.stop();
        log.info("csv file took " + stopWatch.getLastTaskTimeMillis() + " ms");


//        deleteExistingData();
//        useJpaRepo(); //moc pomale


        deleteExistingData();
        usePreparedStatementNoPK();

        deleteExistingData();
        usePreparedStatement();

        return new ResponseEntity(HttpStatus.OK);

    }


    public void usePreparedStatementNoPK() throws Exception {

        StopWatch stopWatch = new StopWatch();
        log.info("started to insert to DB using prepared statement batch without PK and autoincrement");
        stopWatch.start();
        putDataToTablePreparedStatementWithoutPKandAutoIncrement();
        stopWatch.stop();
        log.info("inserting to DB using prepared statement without PK and Autoincrement took " + stopWatch.getLastTaskTimeMillis() + " ms.");

    }

    @Transactional
    public void useJpaRepo() throws Exception {
        StopWatch stopWatch = new StopWatch();

        log.info("started to insert to DB using jpa repo.save");
        stopWatch.start();
        putDataToTableJpaRepo();
        stopWatch.stop();
        log.info("inserting to DB using jpa repo.save took " + stopWatch.getLastTaskTimeMillis() + " ms.");


    }


    public void usePreparedStatement() throws Exception {
        StopWatch stopWatch = new StopWatch();
        log.info("started to insert to DB using prepared statement batch");
        stopWatch.start();
        putDataToTablePreparedStatement();
        stopWatch.stop();
        log.info("inserting to DB using prepared statement took " + stopWatch.getLastTaskTimeMillis() + " ms.");


    }

    private void putDataToTablePreparedStatementWithoutPKandAutoIncrement() throws Exception {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        removePKandAutoIncrement(connection);
        idCounter = 1L;

// 50 000 000 records to DB. File has 5 000 000 records, reads 10 times
        for (int i = 1; i < 2; i++) { //TODO change  to i<11
            loadCsvFileAndPutToDbPreparedStatementWithoutPKandAutoIncrement(connection);

        }
        putBackPKandAutoIncrement(connection);
        connection.commit();
        connection.close();
    }

    private void loadCsvFileAndPutToDbPreparedStatementWithoutPKandAutoIncrement(Connection connection) throws Exception {
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
            String insertQuery = "Insert into Csvdata (id, version, firstName, lastName) values (?,?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData = null;
            int row = 0;
            while ((rowData = reader.readNext()) != null) {

                addRowToStatementWithoutPK(pstmt, rowData);

                if (row % 100 == 0) {// insert when the batch size is 10
                    pstmt.executeBatch();
                }
                row++;
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            log.error("error reading csv file", e);
            throw new Exception(e);
        }

    }

    private void putBackPKandAutoIncrement(Connection connection) throws SQLException {
        connection.prepareStatement("ALTER TABLE Csvdata" +
                " MODIFY id bigINT AUTO_INCREMENT PRIMARY KEY").execute();
    }

    private void removePKandAutoIncrement(Connection connection) throws SQLException {
        connection.prepareStatement("ALTER TABLE Csvdata\n" +
                "DROP PRIMARY KEY,\n" +
                "CHANGE id id bigint;").execute();
    }

    private void putDataToTableJpaRepo() throws Exception {
        for (int i = 1; i < 2; i++) { //TODO change  to i<11
            loadCsvFileAndPutToDbJpaRepo();

        }
    }

    private void loadCsvFileAndPutToDbJpaRepo() throws Exception {
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
            String[] rowData = null;
            while ((rowData = reader.readNext()) != null) {
                saveRowToJpaRepo(rowData);
            }
        } catch (Exception e) {
            log.error("error reading csv file", e);
            throw new Exception(e);
        }
    }

    private void putDataToTablePreparedStatement() throws Exception {


// 50 000 000 records to DB. File has 5 000 000 records, reads 10 times
        for (int i = 1; i < 2; i++) { //TODO change  to i<11
            loadCsvFileAndPutToDbPreparedStatement();

        }

    }

    private void loadCsvFileAndPutToDbPreparedStatement() throws Exception {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
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
            String insertQuery = "Insert into Csvdata (version, firstName, lastName) values (?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData = null;
            int row = 0;
            while ((rowData = reader.readNext()) != null) {
                addRowToStatement(pstmt, rowData);

                if (row % 100 == 0) {// insert when the batch size is 100
                    pstmt.executeBatch();
                }
                row++;
            }
            pstmt.executeBatch();
            pstmt.close();
        } catch (Exception e) {
            log.error("error reading csv file", e);
            throw new Exception(e);
        }
        connection.commit();
        connection.close();
    }

    private void addRowToStatement(PreparedStatement pstmt, String[] rowData) throws SQLException {
        pstmt.setLong(1, 1L); //version
        int column = 2;
        for (String data : rowData) {
            pstmt.setString(column, data);
            column++;
        }
        pstmt.addBatch();
    }

    private void addRowToStatementWithoutPK(PreparedStatement pstmt, String[] rowData) throws SQLException {
        pstmt.setLong(1, idCounter); //id
        pstmt.setLong(2, 1L); //version
        int column = 3;
        for (String data : rowData) {
            pstmt.setString(column, data);
            column++;
        }
        pstmt.addBatch();
        idCounter++;
    }

    private void saveRowToJpaRepo(String[] rowData) throws SQLException {
        Csvdata csvdata = new Csvdata();
        csvdata.setFirstName(rowData[0]);
        csvdata.setLastName(rowData[1]);
        csvDataRepo.save(csvdata);
    }

    @Transactional
    public void deleteExistingData() {
        csvDataRepo.deleteAll();
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
