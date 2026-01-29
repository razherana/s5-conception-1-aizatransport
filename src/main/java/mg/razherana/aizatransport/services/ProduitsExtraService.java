package mg.razherana.aizatransport.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.ProduitsExtra;
import mg.razherana.aizatransport.repositories.ProduitsExtraRepository;

@Service
@RequiredArgsConstructor
public class ProduitsExtraService {

  private final ProduitsExtraRepository produitsExtraRepository;

  public List<ProduitsExtra> findAll() {
    return produitsExtraRepository.findAll();
  }

  public List<ProduitsExtra> findAllFiltered(String nom, Double prixMin, Double prixMax, String sortBy, String sortOrder) {
    List<ProduitsExtra> produits = produitsExtraRepository.findAll();

    // Filtrage par nom
    if (nom != null && !nom.isEmpty()) {
      produits = produits.stream()
          .filter(p -> p.getNom().toLowerCase().contains(nom.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Filtrage par prix minimum
    if (prixMin != null) {
      produits = produits.stream()
          .filter(p -> p.getPrix() >= prixMin)
          .collect(Collectors.toList());
    }

    // Filtrage par prix maximum
    if (prixMax != null) {
      produits = produits.stream()
          .filter(p -> p.getPrix() <= prixMax)
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<ProduitsExtra> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        produits = produits.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return produits;
  }

  private Comparator<ProduitsExtra> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "nom" -> Comparator.comparing(ProduitsExtra::getNom);
      case "prix" -> Comparator.comparing(ProduitsExtra::getPrix);
      default -> Comparator.comparing(ProduitsExtra::getId);
    };
  }

  public Optional<ProduitsExtra> findById(Integer id) {
    return produitsExtraRepository.findById(id);
  }

  public ProduitsExtra save(ProduitsExtra produitsExtra) {
    return produitsExtraRepository.save(produitsExtra);
  }

  public void deleteById(Integer id) {
    produitsExtraRepository.deleteById(id);
  }
}
