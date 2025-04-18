package pl.spribe.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.spribe.dto.PaymentDTO;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Event;
import pl.spribe.entity.Payment;
import pl.spribe.event.PaymentMadeEvent;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.PaymentRepository;
import pl.spribe.util.Mapper;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final EventService eventService;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          BookingService bookingService,
                          EventService eventService, ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentDTO payForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != Booking.Status.BOOKED) {
            throw new IllegalStateException("Booking is not in BOOKED status");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId).orElseGet(() -> {
            Payment newPayment = new Payment();
            newPayment.setBooking(booking);
            return newPayment;
        });

        payment.setPaid(true);
        payment.setPaidAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        bookingService.payForBooking(bookingId);

        eventService.createEvent(booking.getUnit().getId(), Event.EventType.PAYMENT_MADE,
                "Payment made for booking ID: " + bookingId);

        eventPublisher.publishEvent(new PaymentMadeEvent(this, bookingId));

        return Mapper.paymentToDTO(payment);
    }

    public PaymentDTO getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for booking"));
        return Mapper.paymentToDTO(payment);
    }
}
