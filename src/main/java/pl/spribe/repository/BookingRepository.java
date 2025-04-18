package pl.spribe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.spribe.entity.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
        SELECT b FROM Booking b
        WHERE b.unit.id = :unitId
          AND b.status IN (:activeStatuses)
          AND b.startDate <= :endDate
          AND b.endDate >= :startDate
        """)
    List<Booking> findActiveBookingsForUnit(
            @Param("unitId") Long unitId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activeStatuses") List<Booking.Status> activeStatuses
    );

    default List<Booking> findActiveBookingsForUnit(Long unitId, LocalDate startDate, LocalDate endDate) {
        return findActiveBookingsForUnit(unitId, startDate, endDate, List.of(Booking.Status.BOOKED, Booking.Status.PAID));
    }

    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = 'BOOKED'
          AND b.createdAt <= :cutoff
        """)
    List<Booking> findBookingsToCancel(@Param("cutoff") java.time.LocalDateTime cutoff);

    int countByStatus(Booking.Status status);
}
