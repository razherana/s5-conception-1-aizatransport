package mg.razherana.aizatransport.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Route;
import mg.razherana.aizatransport.models.destinations.RoutePrice;
import mg.razherana.aizatransport.repositories.RoutePriceRepository;
import mg.razherana.aizatransport.repositories.RouteRepository;

@Service
@RequiredArgsConstructor
public class RouteService {

  private final RouteRepository routeRepository;
  private final RoutePriceRepository routePriceRepository;
  // private final DestinationRepository destinationRepository;

  public List<Route> findAll() {
    return routeRepository.findAll();
  }

  public List<Route> findAllFiltered(String departure, String arrival, String sortBy, String sortOrder) {
    List<Route> routes = routeRepository.findAll();

    // Filtrage par départ
    if (departure != null && !departure.isEmpty()) {
      routes = routes.stream()
          .filter(r -> r.getDepartureDestination() != null
              && r.getDepartureDestination().getName().toLowerCase().contains(departure.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par arrivée
    if (arrival != null && !arrival.isEmpty()) {
      routes = routes.stream()
          .filter(r -> r.getArrivalDestination() != null
              && r.getArrivalDestination().getName().toLowerCase().contains(arrival.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Route> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        routes = routes.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return routes;
  }

  private Comparator<Route> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "departure" -> Comparator.comparing(r -> r.getDepartureDestination().getName());
      case "arrival" -> Comparator.comparing(r -> r.getArrivalDestination().getName());
      case "distance" -> Comparator.comparing(Route::getDistanceKm, Comparator.nullsLast(Comparator.naturalOrder()));
      case "id" -> Comparator.comparing(Route::getId);
      default -> null;
    };
  }

  public Optional<Route> findById(Integer id) {
    return routeRepository.findById(id);
  }

  @Transactional
  public Route save(Route route) {
    return routeRepository.save(route);
  }

  @Transactional
  public void deleteById(Integer id) {
    routeRepository.deleteById(id);
  }

  // Price related methods
  public Optional<BigDecimal> getCurrentPrice(Integer routeId) {
    return routePriceRepository.findCurrentPriceForRoute(routeId, LocalDate.now())
        .map(RoutePrice::getPrice);
  }

  public List<RoutePrice> getPriceHistory(Integer routeId) {
    return routePriceRepository.findByRouteIdOrderByEffectiveDateDesc(routeId);
  }

  @Transactional
  public RoutePrice addPrice(Integer routeId, BigDecimal price, LocalDate effectiveDate) {
    Route route = routeRepository.findById(routeId)
        .orElseThrow(() -> new RuntimeException("Route non trouvée"));

    RoutePrice routePrice = new RoutePrice();
    routePrice.setRoute(route);
    routePrice.setPrice(price);
    routePrice.setEffectiveDate(effectiveDate);

    return routePriceRepository.save(routePrice);
  }
}
