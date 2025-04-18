package pl.spribe.service;

import org.springframework.stereotype.Service;
import pl.spribe.dto.EventDTO;
import pl.spribe.entity.Event;
import pl.spribe.entity.Unit;
import pl.spribe.repository.EventRepository;
import pl.spribe.repository.UnitRepository;
import pl.spribe.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UnitRepository unitRepository;

    public EventService(EventRepository eventRepository, UnitRepository unitRepository) {
        this.eventRepository = eventRepository;
        this.unitRepository = unitRepository;
    }

    public EventDTO createEvent(Long unitId, Event.EventType eventType, String description) {
        Event event = new Event();
        if (unitId != null) {
            Unit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
            event.setUnit(unit);
        }
        event.setEventType(eventType);
        event.setDescription(description);
        event = eventRepository.save(event);
        return Mapper.eventsToDTO(event);
    }

    public List<EventDTO> getEventsByUnit(Long unitId) {
        List<Event> events = eventRepository.findByUnitId(unitId);
        return events.stream().map(Mapper::eventsToDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByType(Event.EventType eventType) {
        List<Event> events = eventRepository.findByEventType(eventType);
        return events.stream().map(Mapper::eventsToDTO).collect(Collectors.toList());
    }
}
