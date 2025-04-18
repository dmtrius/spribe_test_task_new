package pl.spribe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StatisticsDTO {
    private int totalUnits;
    private int availableUnits;
    private int bookedUnits;
    private int paidUnits;
    private int cancelledUnits;
}
