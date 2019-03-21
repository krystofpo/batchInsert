package cz.krystofample.demolibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(LibraryConfig.class)
public class DemolibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemolibraryApplication.class, args);
    }

}
