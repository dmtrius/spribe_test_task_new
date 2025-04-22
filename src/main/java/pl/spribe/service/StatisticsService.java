package pl.spribe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.spribe.dto.StatisticsDTO;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Unit;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.UnitRepository;

import java.util.List;

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
                             @Qualifier("mainHazel") HazelcastInstance hazelcastInstance) {
        this.unitRepository = unitRepository;
        this.bookingRepository = bookingRepository;
        this.hazelcastInstance = hazelcastInstance;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application is ready, initializing StatisticsService");
        init();
        log.info("StatisticsService initialized successfully");
    }

    // @PostConstruct
    public void init() {
        log.info("Accessing Hazelcast map");
        cache = hazelcastInstance.getMap("unitAvailabilityCache");
        log.info("Initialization completed");
        log.info("Initializing StatisticsService");
        try {
            IMap<String, Object> jobRecords = hazelcastInstance.getMap("__jet.jobs");
            jobRecords.putIfAbsent("init", "done");
            log.info("Job records IMap initialized");
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

    @Transactional(readOnly = true)
    public StatisticsDTO getStatisticsSummary() {
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
