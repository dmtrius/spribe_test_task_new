package pl.spribe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.spribe.dto.StatisticsDTO;
import pl.spribe.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Statistics related to Units and Bookings")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/units/available")
    @Operation(summary = "Get number of Units currently available for booking",
            description = "Returns the cached count of Units that are available for booking.")
    public ResponseEntity<Integer> getAvailableUnitsCount() {
        int count = statisticsService.getAvailableUnitsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get summary statistics about Units and Bookings",
            description = "Returns counts of total Units, available Units, and bookings by status.")
    public ResponseEntity<StatisticsDTO> getStatisticsSummary() {
        StatisticsDTO stats = statisticsService.getStatisticsSummary();
        return ResponseEntity.ok(stats);
    }
}
