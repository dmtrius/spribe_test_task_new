package pl.spribe.dto;

import lombok.Data;
import pl.spribe.entity.Unit;

import java.math.BigDecimal;

@Data
public class UnitDTO {
    private Long id;
    private Integer rooms;
    private Unit.AccommodationType accommodationType;
    private Integer floor;
    private BigDecimal cost;
    private String description;
}
