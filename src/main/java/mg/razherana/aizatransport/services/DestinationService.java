package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Destination;
import mg.razherana.aizatransport.repositories.DestinationRepository;

@Service
@RequiredArgsConstructor
public class DestinationService {

  private final DestinationRepository destinationRepository;

  public List<Destination> findAll() {
    return destinationRepository.findAll();
  }

  public List<Destination> findAllFiltered(String name, String sortBy, String sortOrder) {
    List<Destination> destinations = destinationRepository.findAll();

    // Filtrage par nom
    if (name != null && !name.isEmpty()) {
      destinations = destinations.stream()
          .filter(d -> d.getName().toLowerCase().contains(name.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Destination> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        destinations = destinations.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return destinations;
  }

  private Comparator<Destination> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "name" -> Comparator.comparing(Destination::getName);
      case "id" -> Comparator.comparing(Destination::getId);
      default -> null;
    };
  }

  public Optional<Destination> findById(Integer id) {
    return destinationRepository.findById(id);
  }

  public Destination save(Destination destination) {
    return destinationRepository.save(destination);
  }

  public void deleteById(Integer id) {
    destinationRepository.deleteById(id);
  }
}
