package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Facture;
import mg.razherana.aizatransport.repositories.FactureRepository;

@Service
@RequiredArgsConstructor
public class FactureService {

  private final FactureRepository factureRepository;

  public List<Facture> findAll() {
    return factureRepository.findAll();
  }

  public List<Facture> findAllFiltered(String clientName, String sortBy, String sortOrder) {
    List<Facture> factures = factureRepository.findAllWithDetails();

    // Filtrage par nom de client
    if (clientName != null && !clientName.isEmpty()) {
      factures = factures.stream()
          .filter(f -> f.getClient() != null &&
              f.getClient().getFullName().toLowerCase().contains(clientName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Facture> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        factures = factures.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return factures;
  }

  private Comparator<Facture> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "facturedate" -> Comparator.comparing(Facture::getFactureDate, 
          Comparator.nullsLast(Comparator.naturalOrder()));
      case "ref" -> Comparator.comparing(Facture::getRef);
      case "client" -> Comparator.comparing(f -> f.getClient() != null ? f.getClient().getFullName() : "");
      default -> null;
    };
  }

  public Optional<Facture> findById(Integer id) {
    return factureRepository.findById(id);
  }

  public Optional<Facture> findByIdWithDetails(Integer id) {
    return factureRepository.findByIdWithDetails(id);
  }

  public Facture save(Facture facture) {
    return factureRepository.save(facture);
  }

  public void deleteById(Integer id) {
    factureRepository.deleteById(id);
  }

  public List<Facture> findAllByClientId(Integer clientId) {
    return factureRepository.findAllByClientId(clientId);
  }

  /**
   * Calculate the total amount of all diffusions in a facture
   */
  public Double calculateTotalAmount(Facture facture) {
    if (facture == null || facture.getFactureDiffusionFilles() == null || facture.getFactureDiffusionFilles().isEmpty()) {
      return 0.0;
    }
    return facture.getFactureDiffusionFilles().stream()
        .filter(fdf -> fdf.getDiffusion() != null)
        .mapToDouble(fdf -> fdf.getDiffusion().getAmount() != null ? fdf.getDiffusion().getAmount() : 0.0)
        .sum();
  }

  /**
   * Calculate the total amount already paid for a facture
   */
  public Double calculateAmountPaid(Facture facture) {
    if (facture == null || facture.getFacturePayments() == null || facture.getFacturePayments().isEmpty()) {
      return 0.0;
    }
    return facture.getFacturePayments().stream()
        .mapToDouble(fp -> fp.getAmount() != null ? fp.getAmount() : 0.0)
        .sum();
  }

  /**
   * Calculate the remaining amount to be paid for a facture
   */
  public Double calculateAmountRemaining(Facture facture) {
    if (facture == null) {
      return 0.0;
    }
    double total = calculateTotalAmount(facture);
    double paid = calculateAmountPaid(facture);
    return total - paid;
  }

  /**
   * Check if a facture is fully paid
   */
  public boolean isFullyPaid(Facture facture) {
    return calculateAmountRemaining(facture) <= 0.001; // Using small epsilon for double comparison
  }
}
