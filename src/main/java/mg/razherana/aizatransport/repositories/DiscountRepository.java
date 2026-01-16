package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    List<Discount> findByRouteId(Integer routeId);
    List<Discount> findByDiscountTypeId(Integer discountTypeId);
    List<Discount> findByRouteIdAndTripTypeIdAndSeatTypeId(Integer routeId, Integer tripTypeId, Integer seatTypeId);
}
