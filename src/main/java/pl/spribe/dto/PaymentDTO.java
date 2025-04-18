package pl.spribe.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private boolean paid;
    private LocalDateTime paidAt;
}
