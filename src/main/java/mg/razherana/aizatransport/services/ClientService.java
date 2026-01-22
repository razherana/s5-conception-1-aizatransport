package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Client;
import mg.razherana.aizatransport.repositories.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;

  public List<Client> findAll() {
    return clientRepository.findAll();
  }

  public List<Client> findAllFiltered(String fullName, Integer clientTypeId, String sortBy, String sortOrder) {
    List<Client> clients = clientRepository.findAll();

    // Filtrage par nom complet
    if (fullName != null && !fullName.isEmpty()) {
      clients = clients.stream()
          .filter(p -> p.getFullName().toLowerCase().contains(fullName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par type de client
    if (clientTypeId != null) {
      clients = clients.stream()
          .filter(c -> c.getClientType() != null && c.getClientType().getId().equals(clientTypeId))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Client> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        clients = clients.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return clients;
  }

  private Comparator<Client> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "fullname" -> Comparator.comparing(Client::getFullName);
      case "phone" -> Comparator.comparing(Client::getPhone);
      default -> Comparator.comparing(Client::getId);
    };
  }

  public Optional<Client> findById(Integer id) {
    return clientRepository.findById(id);
  }

  public Client save(Client client) {
    return clientRepository.save(client);
  }

  public void deleteById(Integer id) {
    clientRepository.deleteById(id);
  }
}
