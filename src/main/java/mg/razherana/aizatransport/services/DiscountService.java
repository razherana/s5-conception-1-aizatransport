package mg.razherana.aizatransport.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Discount;
import mg.razherana.aizatransport.models.destinations.DiscountType;
import mg.razherana.aizatransport.models.destinations.Passenger;
import mg.razherana.aizatransport.models.destinations.Route;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.models.destinations.TripType;
import mg.razherana.aizatransport.models.transports.SeatType;
import mg.razherana.aizatransport.repositories.DiscountRepository;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    public Optional<Discount> findById(Integer id) {
        return discountRepository.findById(id);
    }

    public List<Discount> findByRouteId(Integer routeId) {
        return discountRepository.findByRouteId(routeId);
    }

    public List<Discount> findByDiscountTypeId(Integer discountTypeId) {
        return discountRepository.findByDiscountTypeId(discountTypeId);
    }

    public List<Discount> findByRouteIdAndTripTypeIdAndSeatTypeId(Integer routeId, Integer tripTypeId, Integer seatTypeId) {
        return discountRepository.findByRouteIdAndTripTypeIdAndSeatTypeId(routeId, tripTypeId, seatTypeId);
    }

    @Transactional
    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }

    @Transactional
    public void deleteById(Integer id) {
        discountRepository.deleteById(id);
    }

    /**
     * Creates a discount for a specific route, trip type, and seat type combination.
     * 
     * @param route The route to apply the discount to
     * @param tripType The trip type
     * @param seatType The seat type
     * @param discountType The type of discount
     * @param amount The discount amount (can be null if percentage is set)
     * @param percentage The discount percentage (can be null if amount is set)
     * @param effectiveDate The date when the discount becomes effective
     * @return The created discount
     */
    @Transactional
    public Discount createDiscount(Route route, TripType tripType, SeatType seatType, 
                                   DiscountType discountType, Double amount, Double percentage, LocalDate effectiveDate) {
        Discount discount = new Discount();
        discount.setRoute(route);
        discount.setTripType(tripType);
        discount.setSeatType(seatType);
        discount.setDiscountType(discountType);
        discount.setAmount(amount);
        discount.setPercentage(percentage);
        discount.setEffectiveDate(effectiveDate);
        return discountRepository.save(discount);
    }

    /**
     * Gets the applicable discount for a passenger on a specific trip and seat type.
     * Uses the DiscountType's age and comparator fields to determine eligibility.
     * 
     * @param trip The trip for the reservation
     * @param seatType The seat type selected
     * @param passenger The passenger making the reservation
     * @return The applicable discount, or null if no discount applies
     */
    public Discount getDiscountfor(Trip trip, SeatType seatType, Passenger passenger){
        if (passenger.getBirthDate() == null) {
            return null; // No discount if birth date is not available
        }
        
        int passengerAge = passenger.getAge(LocalDate.now());
        LocalDate now = LocalDate.now();
        
        // Get all discounts for this route/trip/seat combination
        List<Discount> discounts = findByRouteIdAndTripTypeIdAndSeatTypeId(
            trip.getRoute().getId(), 
            trip.getTripType().getId(), 
            seatType.getId()
        );
        
        // Find the first discount that matches the passenger's age and is currently effective
        return discounts.stream()
            .filter(d -> !d.getEffectiveDate().isAfter(now)) // Must be effective
            .filter(d -> matchesAgeRequirement(passengerAge, d.getDiscountType()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if a passenger's age matches the discount type's age requirement.
     * 
     * @param passengerAge The passenger's age
     * @param discountType The discount type with age and comparator
     * @return true if the age requirement is met, false otherwise
     */
    private boolean matchesAgeRequirement(int passengerAge, DiscountType discountType) {
        int requiredAge = discountType.getAge();
        String comparator = discountType.getComparator();
        
        return switch (comparator) {
            case "LT" -> passengerAge < requiredAge;
            case "LTE" -> passengerAge <= requiredAge;
            case "EQ" -> passengerAge == requiredAge;
            case "GT" -> passengerAge > requiredAge;
            case "GTE" -> passengerAge >= requiredAge;
            default -> false;
        };
    }
}
