package pl.spribe.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDTO {
    private Long unitId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
