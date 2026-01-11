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

    public List<ExpenseType> findAll(){
        return expenseTypeRepository.findAll();
    }

    public Optional<ExpenseType> findById(Integer id){
        return expenseTypeRepository.findById(id);
    }

    public ExpenseType save(ExpenseType expenseType){
        return expenseTypeRepository.save(expenseType);
    }

    public void deleteById(Integer id){
        expenseTypeRepository.deleteById(id);
    }
}
