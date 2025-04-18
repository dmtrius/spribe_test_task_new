package pl.spribe.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.spribe.entity.Booking;
import pl.spribe.repository.BookingRepository;
import pl.spribe.repository.PaymentRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PaymentControllerIntegrationTest {

    @Container
    private PostgreSQLContainer<?> postgres;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    PaymentRepository paymentRepository;

    @SuppressWarnings("resource")
    @BeforeAll
    public void init() {
        postgres = new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");
    }

    @AfterAll
    public void shutdown() {
        postgres.close();
    }

    @Test
    void payForBooking_endpointWorks() throws Exception {
        Booking booking = new Booking();
        // ...set fields...
        booking.setStatus(Booking.Status.BOOKED);
        booking = bookingRepository.save(booking);

        mockMvc.perform(post("/api/payments/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":" + booking.getId() + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid").value(true));
    }
}
