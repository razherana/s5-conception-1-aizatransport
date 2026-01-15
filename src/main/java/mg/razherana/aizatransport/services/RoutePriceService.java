package mg.razherana.aizatransport.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.RoutePrice;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.repositories.RoutePriceRepository;

@Service
@RequiredArgsConstructor
public class RoutePriceService {

  private final RoutePriceRepository routePriceRepository;

  public List<RoutePrice> findAll() {
    return routePriceRepository.findAll();
  }

  /**
   * Calculate price map for a trip.
   * Returns a map with composite key: routeId_tripTypeId_seatTypeId -> price
   * Uses the latest effective route price that is valid at the trip's departure
   * date.
   */
  public Map<String, BigDecimal> calculatePriceMapForTrip(Trip trip) {
    return calculatePriceMap(
        trip.getDepartureDatetime().toLocalDate(),
        stream -> stream.filter(
            rp -> rp.getRoute().getId().equals(trip.getRoute().getId())));
  }

  /**
   * Calculate price map.
   * 
   * Format of the map key: routeId_tripTypeId_seatTypeId -> price
   * 
   * @param effectiveDate
   * @param routePriceStreamModifier
   * @return
   */
  public Map<String, BigDecimal> calculatePriceMap(
      LocalDate effectiveDate,
      Function<Stream<RoutePrice>, Stream<RoutePrice>> routePriceStreamModifier) {
    List<RoutePrice> routePrices = routePriceRepository.findAll();

    routePrices = routePrices.stream()
        .filter(rp -> !rp.getEffectiveDate().isAfter(effectiveDate))
        .sorted((a, b) -> b.getEffectiveDate().compareTo(a.getEffectiveDate())) // Sort descending (latest first)
        .toList();

    if (routePriceStreamModifier != null)
      routePrices = routePriceStreamModifier.apply(routePrices.stream()).toList();

    Map<String, BigDecimal> priceMap = new HashMap<>();

    for (RoutePrice rp : routePrices) {
      String key = rp.getRoute().getId() + "_" + rp.getTripType().getId() + "_" + rp.getSeatType().getId();
      priceMap.put(key, rp.getPrice());
    }

    return priceMap;
  }
}
