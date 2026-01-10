package mg.razherana.aizatransport.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.repositories.TripRepository;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;

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
}
