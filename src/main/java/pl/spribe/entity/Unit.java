package pl.spribe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Entity
@Table(name = "units")
public class Unit {

    public enum AccommodationType {
        HOME, FLAT, APARTMENTS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rooms;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_type")
    private AccommodationType accommodationType;

    private int floor;

    private BigDecimal cost;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Booking> bookings;

}
