package pl.spribe.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.spribe.dto.UnitDTO;
import pl.spribe.entity.Unit;
import pl.spribe.repository.UnitRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UnitServiceTest {

    private final UnitRepository unitRepository = Mockito.mock(UnitRepository.class);
    private final UnitService unitService = new UnitService(unitRepository);

    @Test
    void createUnit_ShouldSaveAndReturnDTO() {
        Unit unit = new Unit();
        unit.setId(1L);
        unit.setRooms(2);
        unit.setAccommodationType(Unit.AccommodationType.FLAT);
        unit.setFloor(3);
        unit.setCost(BigDecimal.valueOf(100));
        unit.setDescription("Test unit");

        Mockito.when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        UnitDTO dto = new UnitDTO();
        dto.setRooms(2);
        dto.setAccommodationType(Unit.AccommodationType.FLAT);
        dto.setFloor(3);
        dto.setCost(BigDecimal.valueOf(100));
        dto.setDescription("Test unit");

        UnitDTO result = unitService.createUnit(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(dto.getRooms(), result.getRooms());
        assertEquals(dto.getAccommodationType(), result.getAccommodationType());
    }
}
