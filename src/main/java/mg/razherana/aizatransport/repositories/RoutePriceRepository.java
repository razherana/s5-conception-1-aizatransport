package mg.razherana.aizatransport.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.RoutePrice;

@Repository
public interface RoutePriceRepository extends JpaRepository<RoutePrice, Integer> {
  @EntityGraph(attributePaths = { "route" })
  @Query("SELECT rp FROM RoutePrice rp WHERE rp.route.id = :routeId ORDER BY rp.effectiveDate DESC")
  List<RoutePrice> findByRouteIdOrderByEffectiveDateDesc(@Param("routeId") Integer routeId);

  default Optional<RoutePrice> findCurrentPriceForRoute(Integer routeId, LocalDate date) {
    List<RoutePrice> prices = findByRouteIdOrderByEffectiveDateDesc(routeId);
    prices = prices.stream()
        .filter(rp -> !rp.getEffectiveDate().isAfter(date))
        .toList();
    return prices.isEmpty() ? Optional.empty() : Optional.of(prices.get(0));
  }
}
