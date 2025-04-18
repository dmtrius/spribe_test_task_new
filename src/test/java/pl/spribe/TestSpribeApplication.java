package pl.spribe;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestSpribeApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpribeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
