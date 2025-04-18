package pl.spribe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.spribe.dto.PaymentDTO;
import pl.spribe.dto.PaymentRequestDTO;
import pl.spribe.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Operations related to Payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    @Operation(summary = "Make payment for a booking")
    public ResponseEntity<PaymentDTO> payForBooking(@Valid @RequestBody PaymentRequestDTO request) {
        try {
            PaymentDTO paymentDTO = paymentService.payForBooking(request.getBookingId());
            return ResponseEntity.ok(paymentDTO);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payment details by Booking ID")
    public ResponseEntity<PaymentDTO> getPaymentByBooking(@PathVariable Long bookingId) {
        try {
            PaymentDTO paymentDTO = paymentService.getPaymentByBooking(bookingId);
            return ResponseEntity.ok(paymentDTO);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
