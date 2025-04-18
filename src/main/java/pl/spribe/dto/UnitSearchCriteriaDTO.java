package pl.spribe.dto;

import lombok.Data;
import pl.spribe.entity.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UnitSearchCriteriaDTO {
    private Integer rooms;
    private Unit.AccommodationType accommodationType;
    private Integer floor;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minCost;
    private BigDecimal maxCost;
}
