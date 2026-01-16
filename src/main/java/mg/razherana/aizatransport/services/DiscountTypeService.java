package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.DiscountType;
import mg.razherana.aizatransport.repositories.DiscountTypeRepository;

@Service
@RequiredArgsConstructor
public class DiscountTypeService {
  private final DiscountTypeRepository discountTypeRepository;

  public List<DiscountType> findAll() {
    return discountTypeRepository.findAll();
  }

  public Optional<DiscountType> findById(Integer id) {
    return discountTypeRepository.findById(id);
  }

  public DiscountType save(DiscountType discountType) {
    return discountTypeRepository.save(discountType);
  }

  public void deleteById(Integer id) {
    discountTypeRepository.deleteById(id);
  }

  public List<DiscountType> findAllFiltered(String name, String sortBy, String sortOrder) {
    List<DiscountType> discountTypes = discountTypeRepository.findAll();

    discountTypes.removeIf(dt -> name != null && !name.isBlank() &&
        !dt.getName().toLowerCase().contains(name.toLowerCase()));

    discountTypes.sort((dt1, dt2) -> {
      int comparison = 0;
      if ("name".equalsIgnoreCase(sortBy)) {
        comparison = dt1.getName().compareToIgnoreCase(dt2.getName());
      }

      if ("id".equalsIgnoreCase(sortBy)) {
        comparison = dt1.getId().compareTo(dt2.getId());
      }

      return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
    });

    return discountTypes;
  }
}
