package cz.krystofample.demolibrary;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackageClasses = TestUtil.class)
@Import(DemolibraryApplication.class)
public class TestConfig {


}
