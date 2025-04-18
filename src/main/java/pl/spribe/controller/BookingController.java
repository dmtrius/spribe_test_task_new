package pl.spribe.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.spribe.dto.BookingDTO;
import pl.spribe.dto.BookingRequestDTO;
import pl.spribe.entity.Booking;
import pl.spribe.service.BookingService;
import pl.spribe.util.Mapper;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Operations related to Bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    @Operation(summary = "Book a Unit for a user")
    public ResponseEntity<BookingDTO> bookUnit(@Valid @RequestBody BookingRequestDTO request) {
        try {
            Booking booking = bookingService.bookUnit(
                    request.getUnitId(),
                    request.getUserId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Mapper.bookingToDTO(booking));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a Booking by ID")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{bookingId}/pay")
    @Operation(summary = "Pay for a Booking by ID")
    public ResponseEntity<Void> payForBooking(@PathVariable Long bookingId) {
        try {
            bookingService.payForBooking(bookingId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get Booking details by ID")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.getBooking(bookingId);
        return Optional.ofNullable(booking)
                .map(b -> ResponseEntity.ok(Mapper.bookingToDTO(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
