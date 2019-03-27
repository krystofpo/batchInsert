package cz.krystof.csvimport;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = TestUtil.class)
@Import(ImportApplication.class)
public class TestConfig {


}
