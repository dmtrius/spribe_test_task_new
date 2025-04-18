package pl.spribe.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Unit;
import pl.spribe.entity.User;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.UnitRepository;
import pl.spribe.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final StatisticsService statisticsService;

    public BookingService(BookingRepository bookingRepository,
                          UnitRepository unitRepository,
                          UserRepository userRepository,
                          StatisticsService statisticsService) {
        this.bookingRepository = bookingRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.statisticsService = statisticsService;
    }

    @Transactional(readOnly = true)
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    @Transactional
    public Booking bookUnit(Long unitId, Long userId, LocalDate startDate, LocalDate endDate) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new IllegalArgumentException("Unit not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean available = bookingRepository.findActiveBookingsForUnit(unitId, startDate, endDate).isEmpty();
        if (!available) {
            throw new IllegalStateException("Unit is not available for the selected dates");
        }

        Booking booking = new Booking();
        booking.setUnit(unit);
        booking.setUser(user);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(Booking.Status.BOOKED);
        booking.setCreatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        statisticsService.updateCacheAfterBookingChange();

        return booking;
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);

        statisticsService.updateCacheAfterBookingChange();
    }

    @Transactional
    public void payForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (booking.getStatus() != Booking.Status.BOOKED) {
            throw new IllegalStateException("Booking is not in BOOKED status");
        }
        booking.setStatus(Booking.Status.PAID);
        bookingRepository.save(booking);

        statisticsService.updateCacheAfterBookingChange();
    }

    @Scheduled(fixedRateString = "${schedule.cancel_unpaid_bookings_rate}")
    @Transactional
    public void cancelUnpaidBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
        var unpaidBookings = bookingRepository.findBookingsToCancel(cutoff);
        for (Booking booking : unpaidBookings) {
            booking.setStatus(Booking.Status.CANCELLED);
            bookingRepository.save(booking);
        }
        if (!unpaidBookings.isEmpty()) {
            statisticsService.updateCacheAfterBookingChange();
        }
    }
}
