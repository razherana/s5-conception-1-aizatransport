package mg.razherana.aizatransport.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.transports.Vehicle;
import mg.razherana.aizatransport.repositories.VehicleRepository;

@Service
@RequiredArgsConstructor
public class VehicleService {

  private final VehicleRepository vehicleRepository;

  public List<Vehicle> findAll() {
    return vehicleRepository.findAll();
  }

  public List<Vehicle> findAllFiltered(String brand, String status, String sortBy, String sortOrder) {
    List<Vehicle> vehicles = vehicleRepository.findAll();

    // Filtrage par marque
    if (brand != null && !brand.isEmpty()) {
      vehicles = vehicles.stream()
          .filter(v -> v.getBrand().toLowerCase().contains(brand.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par statut
    if (status != null && !status.isEmpty()) {
      vehicles = vehicles.stream()
          .filter(v -> v.getStatus().equalsIgnoreCase(status))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Vehicle> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        vehicles = vehicles.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return vehicles;
  }

  private Comparator<Vehicle> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "brand" -> Comparator.comparing(Vehicle::getBrand);
      case "model" -> Comparator.comparing(Vehicle::getModel);
      case "capacity" -> Comparator.comparing(Vehicle::getCapacity);
      case "status" -> Comparator.comparing(Vehicle::getStatus);
      case "createdat" -> Comparator.comparing(Vehicle::getCreatedAt);
      case "platenumber" -> Comparator.comparing(Vehicle::getPlateNumber);
      default -> null;
    };
  }

  public Optional<Vehicle> findById(Integer id) {
    return vehicleRepository.findById(id);
  }

  public Vehicle save(Vehicle vehicle) {
    if (vehicle.getCreatedAt() == null) {
      vehicle.setCreatedAt(LocalDate.now());
    }
    if (vehicle.getActive() == null) {
      vehicle.setActive(true);
    }
    return vehicleRepository.save(vehicle);
  }

  public void deleteById(Integer id) {
    vehicleRepository.deleteById(id);
  }

  public List<String> getAllBrands() {
    return vehicleRepository.findAll().stream()
        .map(Vehicle::getBrand)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

  public List<String> getAllStatuses() {
    return Arrays.stream(Vehicle.VehicleStatus.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
