package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Passenger;
import mg.razherana.aizatransport.repositories.PassengerRepository;

@Service
@RequiredArgsConstructor
public class PassengerService {

  private final PassengerRepository passengerRepository;

  public List<Passenger> findAll() {
    return passengerRepository.findAll();
  }

  public List<Passenger> findAllFiltered(String fullName, String sortBy, String sortOrder) {
    List<Passenger> passengers = passengerRepository.findAll();

    // Filtrage par nom complet
    if (fullName != null && !fullName.isEmpty()) {
      passengers = passengers.stream()
          .filter(p -> p.getFullName().toLowerCase().contains(fullName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Passenger> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        passengers = passengers.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return passengers;
  }

  private Comparator<Passenger> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "fullname" -> Comparator.comparing(Passenger::getFullName);
      case "phone" -> Comparator.comparing(Passenger::getPhone);
      default -> Comparator.comparing(Passenger::getId);
    };
  }

  public Optional<Passenger> findById(Integer id) {
    return passengerRepository.findById(id);
  }

  public Passenger save(Passenger passenger) {
    return passengerRepository.save(passenger);
  }

  public void deleteById(Integer id) {
    passengerRepository.deleteById(id);
  }
}
