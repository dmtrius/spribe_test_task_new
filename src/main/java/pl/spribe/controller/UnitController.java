package pl.spribe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.spribe.dto.UnitDTO;
import pl.spribe.dto.UnitSearchCriteriaDTO;
import pl.spribe.service.UnitService;

@RestController
@RequestMapping("/api/units")
@Tag(name = "Units", description = "Operations related to Units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    @Operation(summary = "Create a new Unit")
    public UnitDTO createUnit(@RequestBody UnitDTO dto) {
        return unitService.createUnit(dto);
    }

    @PostMapping("/search")
    @Operation(summary = "Search Units by criteria with pagination and sorting")
    public Page<UnitDTO> searchUnits(@RequestBody UnitSearchCriteriaDTO criteria,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "cost") String sortBy,
                                     @RequestParam(defaultValue = "true") boolean asc) {
        return unitService.searchUnits(criteria, page, size, sortBy, asc);
    }
}
