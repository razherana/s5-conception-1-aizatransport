package mg.razherana.aizatransport.services;

import java.util.Arrays;
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

  public List<Diffusion> findAllFiltered(String clientName, String status, String sortBy, String sortOrder) {
    List<Diffusion> diffusions = diffusionRepository.findAllWithPayments();

    // Filtrage par nom de client
    if (clientName != null && !clientName.isEmpty()) {
      diffusions = diffusions.stream()
          .filter(d -> d.getClient() != null &&
              d.getClient().getFullName().toLowerCase().contains(clientName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par statut
    if (status != null && !status.isEmpty()) {
      diffusions = diffusions.stream()
          .filter(d -> d.getStatus().equalsIgnoreCase(status))
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
      case "paymentdate" -> Comparator.comparing(Diffusion::getPaymentDate, 
          Comparator.nullsLast(Comparator.naturalOrder()));
      case "status" -> Comparator.comparing(Diffusion::getStatus);
      case "amount" -> Comparator.comparing(Diffusion::getAmount);
      case "client" -> Comparator.comparing(d -> d.getClient() != null ? d.getClient().getFullName() : "");
      default -> null;
    };
  }

  public Optional<Diffusion> findById(Integer id) {
    return diffusionRepository.findById(id);
  }

  public Optional<Diffusion> findByIdWithPayments(Integer id) {
    return diffusionRepository.findByIdWithPayments(id);
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

  public List<String> getAllStatuses() {
    return Arrays.stream(Diffusion.DiffusionStatus.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }

  public List<Diffusion> findAllByClientId(Integer clientId) {
    return diffusionRepository.findAllByClientIdWithPayments(clientId);
  }

  /**
   * Calculate the total amount already paid for a diffusion
   * Note: Diffusion should be loaded with diffusionFilles collection
   */
  public Double calculateAmountPaid(Diffusion diffusion) {
    if (diffusion == null || diffusion.getDiffusionFilles() == null || diffusion.getDiffusionFilles().isEmpty()) {
      return 0.0;
    }
    return diffusion.getDiffusionFilles().stream()
        .mapToDouble(df -> df.getAmount() != null ? df.getAmount() : 0.0)
        .sum();
  }

  /**
   * Calculate the remaining amount to be paid for a diffusion
   * Note: Diffusion should be loaded with diffusionFilles collection
   */
  public Double calculateAmountRemaining(Diffusion diffusion) {
    if (diffusion == null) {
      return 0.0;
    }
    double total = diffusion.getAmount() != null ? diffusion.getAmount() : 0.0;
    double paid = calculateAmountPaid(diffusion);
    return total - paid;
  }

  /**
   * Check if a diffusion is fully paid
   */
  public boolean isFullyPaid(Diffusion diffusion) {
    return calculateAmountRemaining(diffusion) <= 0.001; // Using small epsilon for double comparison
  }
}
