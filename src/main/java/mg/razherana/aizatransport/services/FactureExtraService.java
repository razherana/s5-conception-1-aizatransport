package mg.razherana.aizatransport.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.controllers.destinations.FactureExtraListDTO;
import mg.razherana.aizatransport.models.destinations.FactureExtra;
import mg.razherana.aizatransport.repositories.FactureExtraRepository;

@Service
@RequiredArgsConstructor
public class FactureExtraService {

  private final FactureExtraRepository factureExtraRepository;

  public List<FactureExtra> findAll() {
    return factureExtraRepository.findAll();
  }

  public List<FactureExtraListDTO> findAllFiltered(Integer clientId, LocalDate dateDebut, LocalDate dateFin, String sortBy, String sortOrder) {
    List<FactureExtra> factures = factureExtraRepository.findAllWithFactureExtraFilles();

    // Filtrage par client
    if (clientId != null) {
      factures = factures.stream()
          .filter(f -> f.getClient() != null && f.getClient().getId().equals(clientId))
          .collect(Collectors.toList());
    }

    // Filtrage par date de début
    if (dateDebut != null) {
      factures = factures.stream()
          .filter(f -> !f.getDate().isBefore(dateDebut))
          .collect(Collectors.toList());
    }

    // Filtrage par date de fin
    if (dateFin != null) {
      factures = factures.stream()
          .filter(f -> !f.getDate().isAfter(dateFin))
          .collect(Collectors.toList());
    }

    // Convert to DTOs (calculate total while we have session)
    List<FactureExtraListDTO> factureDTOs = factures.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<FactureExtraListDTO> comparator = getDTOComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        factureDTOs = factureDTOs.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return factureDTOs;
  }

  private FactureExtraListDTO toDTO(FactureExtra facture) {
    return new FactureExtraListDTO(
        facture.getId(),
        facture.getClient() != null ? facture.getClient().getId() : null,
        facture.getClient() != null ? facture.getClient().getFullName() : null,
        facture.getDate(),
        facture.getTotal()
    );
  }

  private Comparator<FactureExtraListDTO> getDTOComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "date" -> Comparator.comparing(FactureExtraListDTO::getDate);
      case "client" -> Comparator.comparing(f -> f.getClientFullName() != null ? f.getClientFullName() : "");
      case "total" -> Comparator.comparing(FactureExtraListDTO::getTotal);
      default -> Comparator.comparing(FactureExtraListDTO::getId);
    };
  }

  public Optional<FactureExtra> findById(Integer id) {
    return factureExtraRepository.findByIdWithDetails(id);
  }

  @Transactional
  public FactureExtra save(FactureExtra factureExtra) {
    return factureExtraRepository.save(factureExtra);
  }

  public void deleteById(Integer id) {
    factureExtraRepository.deleteById(id);
  }
}
