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

    @InjectMocks
    PaymentService paymentService;

    @Test
    void payForBooking_successfulPayment_marksAsPaidAndCreatesEvent() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Booking.Status.BOOKED);

        Payment payment = new Payment();
        payment.setBooking(booking);

        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        Mockito.when(paymentRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentDTO result = paymentService.payForBooking(1L);

        assertTrue(result.isPaid());
        Mockito.verify(bookingService).payForBooking(1L);
        Mockito.verify(eventService).createEvent(Mockito.anyLong(), Mockito.eq(Event.EventType.PAYMENT_MADE), Mockito.anyString());
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
