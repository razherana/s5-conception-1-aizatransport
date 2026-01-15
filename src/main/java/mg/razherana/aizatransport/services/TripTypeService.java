package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.TripType;
import mg.razherana.aizatransport.repositories.TripTypeRepository;

@Service
@RequiredArgsConstructor
public class TripTypeService {
  private final TripTypeRepository tripTypeRepository;

  public List<TripType> findAll() {
    return tripTypeRepository.findAll();
  }

  public Optional<TripType> findById(Integer id) {
    return tripTypeRepository.findById(id);
  }

  public TripType save(TripType tripType) {
    return tripTypeRepository.save(tripType);
  }

  public void deleteById(Integer id) {
    tripTypeRepository.deleteById(id);
  }

  public List<TripType> findAllFiltered(String name, Boolean active, String sortBy, String sortOrder) {
    List<TripType> tripTypes = tripTypeRepository.findAll();

    tripTypes.removeIf(tt -> name != null && !name.isBlank() &&
        !tt.getName().toLowerCase().contains(name.toLowerCase()));

    tripTypes.removeIf(tt -> active != null && !tt.getActive().equals(active));

    tripTypes.sort((tt1, tt2) -> {
      int comparison = 0;
      if ("name".equalsIgnoreCase(sortBy)) {
        comparison = tt1.getName().compareToIgnoreCase(tt2.getName());
      }

      if ("id".equalsIgnoreCase(sortBy)) {
        comparison = tt1.getId().compareTo(tt2.getId());
      }

      if ("active".equalsIgnoreCase(sortBy)) {
        comparison = tt1.getActive().compareTo(tt2.getActive());
      }

      return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
    });

    return tripTypes;
  }
}
