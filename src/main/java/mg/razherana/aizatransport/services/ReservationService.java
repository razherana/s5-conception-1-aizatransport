package mg.razherana.aizatransport.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.repositories.ReservationRepository;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;

  public List<Reservation> findAll() {
    return reservationRepository.findAll();
  }

  public List<Reservation> findAllFiltered(String passengerName, String status, String sortBy, String sortOrder) {
    List<Reservation> reservations = reservationRepository.findAll();

    // Filtrage par nom de passager
    if (passengerName != null && !passengerName.isEmpty()) {
      reservations = reservations.stream()
          .filter(r -> r.getPassenger() != null &&
              r.getPassenger().getFullName().toLowerCase().contains(passengerName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par statut
    if (status != null && !status.isEmpty()) {
      reservations = reservations.stream()
          .filter(r -> r.getStatus().equalsIgnoreCase(status))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Reservation> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        reservations = reservations.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return reservations;
  }

  private Comparator<Reservation> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "reservationdate" -> Comparator.comparing(Reservation::getReservationDate);
      case "status" -> Comparator.comparing(Reservation::getStatus);
      case "amount" -> Comparator.comparing(Reservation::getAmount);
      case "passenger" -> Comparator.comparing(r -> r.getPassenger() != null ? r.getPassenger().getFullName() : "");
      default -> null;
    };
  }

  public Optional<Reservation> findById(Integer id) {
    return reservationRepository.findById(id);
  }

  public Reservation save(Reservation reservation) {
    if (reservation.getReservationDate() == null) {
      reservation.setReservationDate(LocalDateTime.now());
    }
    return reservationRepository.save(reservation);
  }

  public void deleteById(Integer id) {
    reservationRepository.deleteById(id);
  }

  public List<String> getAllStatuses() {
    return Arrays.stream(Reservation.ReservationStatus.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
