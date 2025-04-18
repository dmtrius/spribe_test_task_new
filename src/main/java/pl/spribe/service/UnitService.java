package pl.spribe.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Expression;
import pl.spribe.dto.UnitDTO;
import pl.spribe.dto.UnitSearchCriteriaDTO;
import pl.spribe.entity.Booking;
import pl.spribe.entity.Unit;
import pl.spribe.repository.UnitRepository;
import pl.spribe.util.Mapper;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public UnitDTO createUnit(UnitDTO dto) {
        Unit unit = new Unit();
        unit.setRooms(dto.getRooms());
        unit.setAccommodationType(dto.getAccommodationType());
        unit.setFloor(dto.getFloor());
        unit.setCost(dto.getCost());
        unit.setDescription(dto.getDescription());
        unit = unitRepository.save(unit);
        return Mapper.unitToDTO(unit);
    }

    public Page<UnitDTO> searchUnits(UnitSearchCriteriaDTO criteria, int page, int size, String sortBy, boolean asc) {
        Pageable pageable = PageRequest.of(page, size, asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);

        Specification<Unit> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (criteria.getRooms() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("rooms"), criteria.getRooms()));
            }
            if (criteria.getAccommodationType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("accommodationType"), criteria.getAccommodationType()));
            }
            if (criteria.getFloor() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("floor"), criteria.getFloor()));
            }
            if (criteria.getMinCost() != null) {
                predicates = cb.and(predicates, cb.ge(root.get("cost"), criteria.getMinCost()));
            }
            if (criteria.getMaxCost() != null) {
                predicates = cb.and(predicates, cb.le(root.get("cost"), criteria.getMaxCost()));
            }
            // Availability: filter out Units that have bookings overlapping with date range
            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                // Subquery to exclude units booked in the date range
                var subquery = Objects.requireNonNull(query).subquery(Long.class);
                var bookingRoot = subquery.from(Booking.class);
                subquery.select(bookingRoot.get("unit").get("id"));
    
                // Convert LocalDate to expressions
                Expression<LocalDate> startDateParam = cb.literal(criteria.getStartDate());
                Expression<LocalDate> endDateParam = cb.literal(criteria.getEndDate());
                
                subquery.where(
                        cb.and(
                                cb.equal(bookingRoot.get("unit").get("id"), root.get("id")),
                                cb.equal(bookingRoot.get("status"), "BOOKED"),
                                cb.or(
                                        // Fixed: now using proper expression parameters
                                        cb.between(startDateParam, bookingRoot.get("startDate"), bookingRoot.get("endDate")),
                                        cb.between(endDateParam, bookingRoot.get("startDate"), bookingRoot.get("endDate")),
                                        cb.and(
                                                cb.lessThanOrEqualTo(bookingRoot.get("startDate"), startDateParam),
                                                cb.greaterThanOrEqualTo(bookingRoot.get("endDate"), endDateParam)
                                        )
                                )
                        )
                );
                predicates = cb.and(predicates, cb.not(cb.exists(subquery)));
            }
            return predicates;
        };

        Page<Unit> units = unitRepository.findAll(spec, pageable);
        return units.map(Mapper::unitToDTO);
    }
}
