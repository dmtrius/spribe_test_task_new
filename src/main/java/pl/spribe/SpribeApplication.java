package pl.spribe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.spribe.entity.Unit;
import pl.spribe.repository.UnitRepository;

import java.math.BigDecimal;
import java.util.Random;

@SpringBootApplication
public class SpribeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpribeApplication.class, args);
    }

    // @Bean
    // CommandLineRunner initUnits(UnitRepository unitRepository) {
    //     return args -> {
    //         Random random = new Random();
    //         for (int i = 0; i < 90; i++) {
    //             Unit unit = new Unit();
    //             unit.setRooms(random.nextInt(5) + 1);
    //             unit.setAccommodationType(Unit.AccommodationType.values()[random.nextInt(Unit.AccommodationType.values().length)]);
    //             unit.setFloor(random.nextInt(10) + 1);
    //             BigDecimal cost = BigDecimal.valueOf(50 + random.nextInt(150));
    //             unit.setCost(cost);
    //             unit.setDescription("Random unit #" + (i + 1));
    //             unitRepository.save(unit);
    //         }
    //     };
    // }
}
