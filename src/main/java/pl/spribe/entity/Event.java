package pl.spribe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    public enum EventType {
        UNIT_CREATED,
        UNIT_UPDATED,
        BOOKING_CREATED,
        BOOKING_CANCELLED,
        PAYMENT_MADE,
        PAYMENT_FAILED,
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ACTIVATED,
        USER_DEACTIVATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
