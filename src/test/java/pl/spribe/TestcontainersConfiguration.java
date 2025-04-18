package pl.spribe;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // Static initialization of Testcontainers
    static {
        // Disable Ryuk container
        System.setProperty("testcontainers.reuse.enable", "true");
        System.setProperty("testcontainers.ryuk.disabled", "true");
    }
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(60))
            .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
            .withTmpFs(java.util.Map.of("/var/lib/postgresql/data", "rw"));
            
        // Ensure the container starts properly
        container.start();
        
        return container;
    }

}
