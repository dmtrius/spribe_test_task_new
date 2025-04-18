package pl.spribe.util;

import lombok.experimental.UtilityClass;
import pl.spribe.dto.*;
import pl.spribe.entity.*;

@UtilityClass
public class Mapper {
    public static BookingDTO bookingToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUnitId(booking.getUnit().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus().name());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }

    public static EventDTO eventsToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setUnitId(event.getUnit() != null ? event.getUnit().getId() : null);
        dto.setEventType(event.getEventType());
        dto.setDescription(event.getDescription());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    public static PaymentDTO paymentToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setPaid(payment.isPaid());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }

    public static UnitDTO unitToDTO(Unit unit) {
        UnitDTO dto = new UnitDTO();
        dto.setId(unit.getId());
        dto.setRooms(unit.getRooms());
        dto.setAccommodationType(unit.getAccommodationType());
        dto.setFloor(unit.getFloor());
        dto.setCost(unit.getCost());
        dto.setDescription(unit.getDescription());
        return dto;
    }

    public static UserDTO userToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
