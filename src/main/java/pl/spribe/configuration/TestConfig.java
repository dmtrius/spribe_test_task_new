package pl.spribe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

@Configuration
public class TestConfig {

    @Bean
    public CommandLineRunner testHazelcast(@Autowired HazelcastInstance hazelcastInstance) {
        return args -> {
            System.out.println("HazelcastInstance: " + hazelcastInstance);
            IMap<String, String> map = hazelcastInstance.getMap("unitAvailabilityCache");
            map.put("test", "value");
            System.out.println("Map value: " + map.get("test"));
        };
    }
}
