package pl.spribe.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.spribe.dto.EventDTO;
import pl.spribe.entity.Event;
import pl.spribe.entity.Unit;
import pl.spribe.repository.EventRepository;
import pl.spribe.repository.UnitRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;
    @Mock
    UnitRepository unitRepository;

    @InjectMocks
    EventService eventService;

    @Test
    void createEvent_withUnit_savesAndReturnsDTO() {
        Unit unit = new Unit();
        unit.setId(10L);

        Mockito.when(unitRepository.findById(10L)).thenReturn(Optional.of(unit));
        Mockito.when(eventRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        EventDTO dto = eventService.createEvent(10L, Event.EventType.UNIT_CREATED, "desc");

        assertEquals(Event.EventType.UNIT_CREATED, dto.getEventType());
        assertEquals(10L, dto.getUnitId());
    }
}
