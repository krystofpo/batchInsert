package cz.krystof.csvimport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
public class ImportSystemTest {


    @Autowired
    ImportSystem importSystem;

    @Autowired
    TestUtil testUtil;

    @Test
    @Transactional
    public void borrowBooks() {


    }


}