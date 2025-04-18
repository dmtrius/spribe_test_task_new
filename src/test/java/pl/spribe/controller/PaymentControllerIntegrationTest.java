package pl.spribe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.spribe.TestcontainersConfiguration;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Unit;
import java.math.BigDecimal;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.PaymentRepository;
import pl.spribe.repository.UnitRepository;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfiguration.class)
class PaymentControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    UnitRepository unitRepository;

    @Test
    void payForBooking_endpointWorks() throws Exception {
        // Create and save a unit first
        Unit unit = new Unit();
        unit.setDescription("Test Unit for testing");
        unit.setAccommodationType(Unit.AccommodationType.FLAT);
        unit.setCost(BigDecimal.valueOf(100.0));
        unit = unitRepository.save(unit);
        
        // Create booking with the unit
        Booking booking = new Booking();
        booking.setUnit(unit);
        booking.setStatus(Booking.Status.BOOKED);
        booking = bookingRepository.save(booking);
        mockMvc.perform(post("/api/payments/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":" + booking.getId() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid").value(true));
    }
}
