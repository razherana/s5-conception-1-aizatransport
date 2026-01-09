package mg.razherana.aizatransport.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.controllers.destinations.RouteStatisticsDTO;
import mg.razherana.aizatransport.models.destinations.Route;
import mg.razherana.aizatransport.models.destinations.RoutePrice;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.repositories.RoutePriceRepository;
import mg.razherana.aizatransport.repositories.RouteRepository;
import mg.razherana.aizatransport.repositories.TripRepository;

@Service
@RequiredArgsConstructor
public class RouteService {

  private final RouteRepository routeRepository;
  private final RoutePriceRepository routePriceRepository;
  private final TripRepository tripRepository;
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

  public RouteStatisticsDTO getRouteStatistics(Integer routeId) {
    List<Trip> trips = tripRepository.findAll().stream()
        .filter(t -> t.getRoute() != null && t.getRoute().getId().equals(routeId))
        .collect(Collectors.toList());

    // Basic statistics
    int totalTrips = trips.size();
    int completedTrips = (int) trips.stream().filter(t -> "TERMINE".equals(t.getStatus())).count();
    int cancelledTrips = (int) trips.stream().filter(t -> "ANNULE".equals(t.getStatus())).count();
    int ongoingTrips = (int) trips.stream().filter(t -> "EN_COURS".equals(t.getStatus())).count();

    // TODO: Revenue calculation use reservations and tickets
    // Revenue calculation (completed trips only)
    BigDecimal totalRevenue = BigDecimal.ZERO;
    for (Trip trip : trips) {
      if ("TERMINE".equals(trip.getStatus())) {
        // Get price at the time of the trip
        Optional<BigDecimal> priceAtTime = routePriceRepository
            .findCurrentPriceForRoute(routeId, trip.getDepartureDatetime().toLocalDate())
            .map(RoutePrice::getPrice);
        
        if (priceAtTime.isPresent() && trip.getVehicle() != null) {
          // Revenue = price * capacity (assuming full vehicle)
          BigDecimal tripRevenue = priceAtTime.get().multiply(new BigDecimal(trip.getVehicle().getCapacity()));
          totalRevenue = totalRevenue.add(tripRevenue);
        }
      }
    }

    BigDecimal averageRevenuePerTrip = completedTrips > 0 
        ? totalRevenue.divide(new BigDecimal(completedTrips), 2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;

    // Trips over time (last 6 months)
    Map<String, Integer> tripsByMonth = new LinkedHashMap<>();
    DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    
    for (int i = 5; i >= 0; i--) {
      LocalDate monthDate = LocalDate.now().minusMonths(i);
      String monthKey = monthDate.format(monthFormatter);
      tripsByMonth.put(monthKey, 0);
    }

    for (Trip trip : trips) {
      String tripMonth = trip.getDepartureDatetime().format(monthFormatter);
      if (tripsByMonth.containsKey(tripMonth)) {
        tripsByMonth.put(tripMonth, tripsByMonth.get(tripMonth) + 1);
      }
    }

    List<String> tripChartLabels = new ArrayList<>(tripsByMonth.keySet());
    List<Integer> tripChartData = new ArrayList<>(tripsByMonth.values());

    // Status distribution
    Map<String, Long> statusCounts = trips.stream()
        .collect(Collectors.groupingBy(Trip::getStatus, Collectors.counting()));
    
    List<String> statusLabels = new ArrayList<>(statusCounts.keySet());
    List<Integer> statusCountsList = statusCounts.values().stream()
        .map(Long::intValue)
        .collect(Collectors.toList());

    // Top drivers
    Map<String, Integer> topDrivers = trips.stream()
        .filter(t -> t.getDriver() != null)
        .collect(Collectors.groupingBy(
            t -> t.getDriver().getFullName(),
            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
        ))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .limit(5)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));

    // Monthly revenue (last 6 months)
    Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
    for (int i = 5; i >= 0; i--) {
      LocalDate monthDate = LocalDate.now().minusMonths(i);
      String monthKey = monthDate.format(monthFormatter);
      revenueByMonth.put(monthKey, BigDecimal.ZERO);
    }

    for (Trip trip : trips) {
      if ("TERMINE".equals(trip.getStatus())) {
        String tripMonth = trip.getDepartureDatetime().format(monthFormatter);
        if (revenueByMonth.containsKey(tripMonth)) {
          Optional<BigDecimal> priceAtTime = routePriceRepository
              .findCurrentPriceForRoute(routeId, trip.getDepartureDatetime().toLocalDate())
              .map(RoutePrice::getPrice);
          
          if (priceAtTime.isPresent() && trip.getVehicle() != null) {
            BigDecimal tripRevenue = priceAtTime.get().multiply(new BigDecimal(trip.getVehicle().getCapacity()));
            revenueByMonth.put(tripMonth, revenueByMonth.get(tripMonth).add(tripRevenue));
          }
        }
      }
    }

    List<String> revenueMonthLabels = new ArrayList<>(revenueByMonth.keySet());
    List<BigDecimal> revenueMonthData = new ArrayList<>(revenueByMonth.values());

    // Get max driver trips for progress bar calculation
    int maxDriverTrips = topDrivers.values().stream()
        .max(Integer::compareTo)
        .orElse(1);

    return new RouteStatisticsDTO(
        totalTrips,
        completedTrips,
        cancelledTrips,
        ongoingTrips,
        totalRevenue,
        averageRevenuePerTrip,
        tripChartLabels,
        tripChartData,
        statusLabels,
        statusCountsList,
        topDrivers,
        maxDriverTrips,
        revenueMonthLabels,
        revenueMonthData
    );
  }
}
