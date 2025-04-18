package pl.spribe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.spribe.dto.EventDTO;
import pl.spribe.entity.Event;
import pl.spribe.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Operations related to Events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Get events by Unit ID")
    public ResponseEntity<List<EventDTO>> getEventsByUnit(@PathVariable Long unitId) {
        List<EventDTO> events = eventService.getEventsByUnit(unitId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/type/{eventType}")
    @Operation(summary = "Get events by Event Type")
    public ResponseEntity<List<EventDTO>> getEventsByType(@PathVariable Event.EventType eventType) {
        List<EventDTO> events = eventService.getEventsByType(eventType);
        return ResponseEntity.ok(events);
    }
}
