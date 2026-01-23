package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.ClientType;
import mg.razherana.aizatransport.models.destinations.Diffusion;
import mg.razherana.aizatransport.repositories.ClientTypeRepository;
import mg.razherana.aizatransport.repositories.DiffusionRepository;

@Service
@RequiredArgsConstructor
public class DiffusionService {

  private final DiffusionRepository diffusionRepository;
  private final ClientTypeRepository clientTypeRepository;

  public List<Diffusion> findAll() {
    return diffusionRepository.findAll();
  }

  public List<Diffusion> findAllFiltered(String clientName, String sortBy, String sortOrder) {
    List<Diffusion> diffusions = diffusionRepository.findAll();

    // Filtrage par nom de client
    if (clientName != null && !clientName.isEmpty()) {
      diffusions = diffusions.stream()
          .filter(d -> d.getClient() != null &&
              d.getClient().getFullName().toLowerCase().contains(clientName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Diffusion> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        diffusions = diffusions.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return diffusions;
  }

  private Comparator<Diffusion> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "amount" -> Comparator.comparing(Diffusion::getAmount, Comparator.nullsLast(Comparator.naturalOrder()));
      case "client" -> Comparator.comparing(d -> d.getClient() != null ? d.getClient().getFullName() : "");
      case "trip" -> Comparator.comparing(d -> d.getTrip() != null && d.getTrip().getId() != null ? d.getTrip().getId() : 0);
      default -> null;
    };
  }

  public Optional<Diffusion> findById(Integer id) {
    return diffusionRepository.findById(id);
  }

  public Diffusion save(Diffusion diffusion) {
    // Auto-assign "Society" client type if the client doesn't have one
    if (diffusion.getClient() != null && diffusion.getClient().getClientType() == null) {
      ClientType societyType = clientTypeRepository.findAll().stream()
          .filter(ct -> "Society".equalsIgnoreCase(ct.getName()) || "Société".equalsIgnoreCase(ct.getName()))
          .findFirst()
          .orElse(null);
      
      if (societyType != null) {
        diffusion.getClient().setClientType(societyType);
      }
    }
    
    return diffusionRepository.save(diffusion);
  }

  public void deleteById(Integer id) {
    diffusionRepository.deleteById(id);
  }

  public List<Diffusion> findAllByClientId(Integer clientId) {
    return diffusionRepository.findAllByClientId(clientId);
  }
}
