package mg.razherana.aizatransport.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.repositories.TripRepository;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final RoutePriceService routePriceService;

  public List<Trip> findAll() {
    return tripRepository.findAll();
  }

  public List<Trip> findAllFiltered(Integer routeId, Integer vehicleId, Integer driverId, String status, String sortBy,
      String sortOrder) {
    return findAllFiltered(routeId, vehicleId, driverId, status, sortBy, sortOrder, false);
  }

  public List<Trip> findAllFiltered(Integer routeId, Integer vehicleId, Integer driverId, String status, String sortBy,
      String sortOrder, boolean joinReservationAndTickets) {

    List<Trip> trips = joinReservationAndTickets ? tripRepository.findAllWithReservationsAndTickets()
        : tripRepository.findAll();

    // Filtrage par route
    if (routeId != null) {
      trips = trips.stream()
          .filter(t -> t.getRoute() != null && t.getRoute().getId().equals(routeId))
          .collect(Collectors.toList());
    }

    // Filtrage par véhicule
    if (vehicleId != null) {
      trips = trips.stream()
          .filter(t -> t.getVehicle() != null && t.getVehicle().getId().equals(vehicleId))
          .collect(Collectors.toList());
    }

    // Filtrage par chauffeur
    if (driverId != null) {
      trips = trips.stream()
          .filter(t -> t.getDriver() != null && t.getDriver().getId().equals(driverId))
          .collect(Collectors.toList());
    }

    // Filtrage par statut
    if (status != null && !status.isEmpty()) {
      trips = trips.stream()
          .filter(t -> t.getStatus().equalsIgnoreCase(status))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Trip> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        trips = trips.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return trips;
  }

  private Comparator<Trip> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "departuredatetime" -> Comparator.comparing(Trip::getDepartureDatetime);
      case "status" -> Comparator.comparing(Trip::getStatus);
      case "route" -> Comparator.comparing(t -> t.getRoute() != null ? t.getRoute().getId() : 0);
      case "vehicle" -> Comparator.comparing(t -> t.getVehicle() != null ? t.getVehicle().getPlateNumber() : "");
      case "driver" -> Comparator.comparing(t -> t.getDriver() != null ? t.getDriver().getFullName() : "");
      default -> null;
    };
  }

  public Optional<Trip> findById(Integer id) {
    return tripRepository.findById(id);
  }

  public Trip save(Trip trip) {
    return tripRepository.save(trip);
  }

  public void deleteById(Integer id) {
    tripRepository.deleteById(id);
  }

  public List<String> getAllStatuses() {
    return Arrays.stream(Trip.TripStatus.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }

  /**
   * Vérifie si un siège est disponible pour un trajet donné
   * 
   * @param tripId L'ID du trajet
   * @param seatId L'ID du siège
   * @return true si le siège est disponible, false sinon
   */
  public boolean isSeatAvailableForTrip(Integer tripId, Integer seatId) {
    Optional<Trip> trip = findById(tripId);
    if (trip.isEmpty()) {
      return false;
    }

    Trip t = trip.get();

    // Vérifier si le siège existe et est actif
    var seat = t.getVehicle().getSeats().stream()
        .filter(s -> s.getId().equals(seatId))
        .findFirst();

    if (seat.isEmpty() || !seat.get().isAvailable()) {
      return false;
    }

    // Vérifier si le siège est déjà réservé (excepté les réservations annulées)
    boolean seatInReservations = t.getReservations().stream()
        .anyMatch(r -> r.getSeat().getId().equals(seatId)
            && !r.getStatus().equalsIgnoreCase("CANCELLED"));

    // Vérifier si le siège est déjà acheté via un ticket
    boolean seatInTickets = t.getTickets().stream()
        .anyMatch(ticket -> ticket.getSeat().getId().equals(seatId));

    return !seatInReservations && !seatInTickets;
  }

  /**
   * Récupère tous les sièges occupés pour un trajet donné
   * 
   * @param tripId L'ID du trajet
   * @return Liste des IDs de sièges occupés
   */
  public List<Integer> getOccupiedSeatsForTrip(Integer tripId) {
    Optional<Trip> trip = findById(tripId);
    if (trip.isEmpty()) {
      return List.of();
    }

    Trip t = trip.get();
    List<Integer> occupiedSeats = new java.util.ArrayList<>();

    // Ajouter les sièges des réservations (excepté les réservations annulées)
    occupiedSeats.addAll(t.getReservations().stream()
        .filter(r -> !r.getStatus().equalsIgnoreCase("CANCELLED"))
        .map(r -> r.getSeat().getId())
        .collect(Collectors.toList()));

    // Ajouter les sièges des tickets
    occupiedSeats.addAll(t.getTickets().stream()
        .map(ticket -> ticket.getSeat().getId())
        .collect(Collectors.toList()));

    return occupiedSeats.stream().distinct().collect(Collectors.toList());
  }

  public Map<Integer, BigDecimal> getMaxCAForEveryTrips(Function<Stream<Trip>, Stream<Trip>> preHandle,
      LocalDate date) {
    Map<Integer, BigDecimal> resultMap = new HashMap<>();

    List<Trip> tripCalculMaxCA = tripRepository.findAllWithVehicleSeatsAndSeatType();

    if (preHandle != null) {
      tripCalculMaxCA = preHandle.apply(tripCalculMaxCA.stream()).toList();
    }

    Map<String, BigDecimal> priceMap = routePriceService.calculatePriceMap(date, null);

    for (Trip trip : tripCalculMaxCA) {
      BigDecimal maxCA = BigDecimal.ZERO;

      // Sum up prices for all seats in the vehicle
      if (trip.getVehicle() != null && trip.getVehicle().getSeats() != null) {
        for (var seat : trip.getVehicle().getSeats()) {
          if (seat.isAvailable() && seat.getSeatType() != null) {
            String priceKey = trip.getRoute().getId() + "_" + trip.getTripType().getId() + "_"
                + seat.getSeatType().getId();
            BigDecimal seatPrice = priceMap.get(priceKey);
            if (seatPrice != null) {
              maxCA = maxCA.add(seatPrice);
            }
          }
        }
      }

      resultMap.put(trip.getId(), maxCA);
    }

    return resultMap;
  }
}
