package pl.spribe.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.spribe.dto.PaymentDTO;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Event;
import pl.spribe.entity.Payment;
import pl.spribe.entity.Unit;
import pl.spribe.event.PaymentMadeEvent;
import org.springframework.context.ApplicationEventPublisher;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.PaymentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock BookingService bookingService;
    @Mock EventService eventService;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks
    PaymentService paymentService;

    @Test
    void payForBooking_successfulPayment_marksAsPaidAndCreatesEvent() {
        // Create a Unit for the Booking
        Unit unit = new Unit();
        unit.setId(2L);
        
        // Set up the Booking with Unit
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Booking.Status.BOOKED);
        booking.setUnit(unit);

        // Set up a complete Payment
        Payment payment = new Payment();
        payment.setId(3L);
        payment.setBooking(booking);
        payment.setPaid(true);
        payment.setPaidAt(java.time.LocalDateTime.now());

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        Mockito.when(paymentRepository.save(Mockito.any())).thenAnswer(inv -> {
            Payment savedPayment = inv.getArgument(0);
            savedPayment.setPaid(true);
            savedPayment.setPaidAt(java.time.LocalDateTime.now());
            return savedPayment;
        });
        PaymentDTO result = paymentService.payForBooking(1L);

        assertTrue(result.isPaid());
        Mockito.verify(bookingService).payForBooking(1L);
        Mockito.verify(eventService).createEvent(Mockito.eq(2L), Mockito.eq(Event.EventType.PAYMENT_MADE), Mockito.anyString());
        Mockito.verify(eventPublisher).publishEvent(Mockito.any(PaymentMadeEvent.class));
    }

    @Test
    void payForBooking_notBooked_throwsException() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Booking.Status.PAID);

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> paymentService.payForBooking(1L));
    }
}
