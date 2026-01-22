package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.ClientType;
import mg.razherana.aizatransport.repositories.ClientTypeRepository;

@Service
@RequiredArgsConstructor
public class ClientTypeService {
  private final ClientTypeRepository clientTypeRepository;

  public List<ClientType> findAll() {
    return clientTypeRepository.findAll();
  }

  public Optional<ClientType> findById(Integer id) {
    return clientTypeRepository.findById(id);
  }

  public ClientType save(ClientType clientType) {
    return clientTypeRepository.save(clientType);
  }

  public void deleteById(Integer id) {
    clientTypeRepository.deleteById(id);
  }

  public List<ClientType> findAllFiltered(String name, Boolean active, String sortBy, String sortOrder) {
    List<ClientType> clientTypes = clientTypeRepository.findAll();

    clientTypes.removeIf(ct -> name != null && !name.isBlank() &&
        !ct.getName().toLowerCase().contains(name.toLowerCase()));

    clientTypes.removeIf(ct -> active != null && !ct.getActive().equals(active));

    clientTypes.sort((ct1, ct2) -> {
      int comparison = 0;
      if ("name".equalsIgnoreCase(sortBy)) {
        comparison = ct1.getName().compareToIgnoreCase(ct2.getName());
      }

      if ("id".equalsIgnoreCase(sortBy)) {
        comparison = ct1.getId().compareTo(ct2.getId());
      }

      if ("active".equalsIgnoreCase(sortBy)) {
        comparison = ct1.getActive().compareTo(ct2.getActive());
      }

      return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
    });

    return clientTypes;
  }
}
