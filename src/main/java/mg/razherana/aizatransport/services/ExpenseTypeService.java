package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.ExpenseType;
import mg.razherana.aizatransport.repositories.ExpenseTypeRepository;

@Service
@RequiredArgsConstructor
public class ExpenseTypeService {
  private final ExpenseTypeRepository expenseTypeRepository;

  public List<ExpenseType> findAll() {
    return expenseTypeRepository.findAll();
  }

  public Optional<ExpenseType> findById(Integer id) {
    return expenseTypeRepository.findById(id);
  }

  public ExpenseType save(ExpenseType expenseType) {
    return expenseTypeRepository.save(expenseType);
  }

  public void deleteById(Integer id) {
    expenseTypeRepository.deleteById(id);
  }

  public List<ExpenseType> findAllFiltered(String typeName, String sortBy, String sortOrder) {
    List<ExpenseType> expenseTypes = expenseTypeRepository.findAll();

    expenseTypes.removeIf(et -> typeName != null && !typeName.isBlank() &&
        !et.getTypeName().toLowerCase().contains(typeName.toLowerCase()));

    expenseTypes.sort((et1, et2) -> {
      int comparison = 0;
      if ("typeName".equalsIgnoreCase(sortBy)) {
        comparison = et1.getTypeName().compareToIgnoreCase(et2.getTypeName());
      }

      if ("id".equalsIgnoreCase(sortBy)) {
        comparison = et1.getId().compareTo(et2.getId());
      }

      return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
    });

    return expenseTypes;
  }
}
