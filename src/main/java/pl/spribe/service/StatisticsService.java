package pl.spribe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import pl.spribe.dto.StatisticsDTO;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Unit;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.UnitRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class StatisticsService {

    private final UnitRepository unitRepository;
    private final BookingRepository bookingRepository;
    private final HazelcastInstance hazelcastInstance;
    private IMap<String, Integer> cache;

    private static final String AVAILABLE_UNITS_KEY = "availableUnitsCount";

    public StatisticsService(UnitRepository unitRepository,
                             BookingRepository bookingRepository,
                             HazelcastInstance hazelcastInstance) {
        this.unitRepository = unitRepository;
        this.bookingRepository = bookingRepository;
        this.hazelcastInstance = hazelcastInstance;
    }

    // @PostConstruct
    public void init() {
        log.info("Starting initialization of StatisticsService");
        if (Objects.isNull(hazelcastInstance)) {
            log.error("HazelcastInstance is null");
            throw new IllegalStateException("HazelcastInstance is not initialized");
        }
        log.info("Accessing Hazelcast map");
        cache = hazelcastInstance.getMap("unitAvailabilityCache");
        log.info("Initialization completed");

        log.info("Initializing StatisticsService");
        try {
            if (!Objects.isNull(hazelcastInstance)) {
                IMap<String, Object> jobRecords = hazelcastInstance.getMap("__jet.jobs");
                jobRecords.putIfAbsent("init", "done");
                log.info("Job records IMap initialized");
            } else {
                log.warn("Hazelcast cluster not ready or HazelcastInstance is null, skipping initialization");
            }
        } catch (Exception e) {
            log.error("Failed to initialize job records IMap", e);
        }

        updateCacheAfterBookingChange();
    }

    public int getAvailableUnitsCount() {
        Integer cached = cache.get(AVAILABLE_UNITS_KEY);
        if (cached == null) {
            cached = calculateAvailableUnitsCount();
            cache.put(AVAILABLE_UNITS_KEY, cached);
        }
        return cached;
    }

    public StatisticsDTO getStatisticsSummary() {
        init();
        if (Objects.isNull(hazelcastInstance) || Objects.isNull(this.cache)) {
            init();
        }
        int totalUnits = (int) unitRepository.count();
        int availableUnits = getAvailableUnitsCount();

        int bookedUnits = bookingRepository.countByStatus(Booking.Status.BOOKED);
        int paidUnits = bookingRepository.countByStatus(Booking.Status.PAID);
        int cancelledUnits = bookingRepository.countByStatus(Booking.Status.CANCELLED);

        return new StatisticsDTO(totalUnits, availableUnits, bookedUnits, paidUnits, cancelledUnits);
    }

    private int calculateAvailableUnitsCount() {
        List<Unit> allUnits = unitRepository.findAll();
        int count = 0;
        for (Unit unit : allUnits) {
            boolean isAvailable = bookingRepository.findActiveBookingsForUnit(
                    unit.getId(),
                    java.time.LocalDate.now(),
                    java.time.LocalDate.now()
            ).isEmpty();
            if (isAvailable) {
                count++;
            }
        }
        return count;
    }

    public void updateCacheAfterBookingChange() {
        List<Unit> units = unitRepository.findAll();
        int availableCount = 0;
        for (Unit unit : units) {
            if (unitIsAvailable(unit.getId())) {
                availableCount++;
            }
        }
        cache.put(AVAILABLE_UNITS_KEY, availableCount);
    }

    private boolean unitIsAvailable(Long unitId) {
        return unitRepository.findById(unitId)
                .map(unit -> unit.getBookings().stream()
                        .noneMatch(b -> b.getStatus() == Booking.Status.BOOKED || b.getStatus() == Booking.Status.PAID))
                .orElse(false);
    }

}
