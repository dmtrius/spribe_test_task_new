package pl.spribe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpribeApplicationTests {

    @Test
    void contextLoads() {
        // The test container is configured and started by TestcontainersConfiguration
    }

}
