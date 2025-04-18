package pl.spribe.dto;


import lombok.Data;
import pl.spribe.entity.Event;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private Long unitId;
    private Event.EventType eventType;
    private String description;
    private LocalDateTime createdAt;
}
