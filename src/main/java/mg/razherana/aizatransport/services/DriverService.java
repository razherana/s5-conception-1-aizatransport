package mg.razherana.aizatransport.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.transports.Driver;
import mg.razherana.aizatransport.repositories.DriverRepository;

@Service
@RequiredArgsConstructor
public class DriverService {

  private final DriverRepository driverRepository;

  public List<Driver> findAll() {
    return driverRepository.findAll();
  }

  public List<Driver> findAllFiltered(String fullName, String status, String sortBy, String sortOrder) {
    List<Driver> drivers = driverRepository.findAll();

    // Filtrage par nom
    if (fullName != null && !fullName.isEmpty()) {
      drivers = drivers.stream()
          .filter(d -> d.getFullName().toLowerCase().contains(fullName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par statut
    if (status != null && !status.isEmpty()) {
      drivers = drivers.stream()
          .filter(d -> d.getStatus().equalsIgnoreCase(status))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Driver> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        drivers = drivers.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return drivers;
  }

  private Comparator<Driver> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "fullname" -> Comparator.comparing(Driver::getFullName);
      case "phone" -> Comparator.comparing(Driver::getPhone, Comparator.nullsLast(Comparator.naturalOrder()));
      case "licensenumber" -> Comparator.comparing(Driver::getLicenseNumber);
      case "status" -> Comparator.comparing(Driver::getStatus);
      case "createdat" -> Comparator.comparing(Driver::getCreatedAt);
      default -> null;
    };
  }

  public Optional<Driver> findById(Integer id) {
    return driverRepository.findById(id);
  }

  public Driver save(Driver driver) {
    if (driver.getCreatedAt() == null) {
      driver.setCreatedAt(java.time.LocalDate.now());
    }
    return driverRepository.save(driver);
  }

  public void deleteById(Integer id) {
    driverRepository.deleteById(id);
  }

  public List<String> getAllStatuses() {
    return Arrays.stream(Driver.DriverStatus.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
