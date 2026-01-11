package mg.razherana.aizatransport.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Expense;
import mg.razherana.aizatransport.repositories.ExpenseRepository;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseTypeService expenseTypeService;
    private final TripService tripService;
    private final VehicleService vehicleService;

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> findAllFiltered(Integer tripId, Integer vehicleId, Integer typeId, String status,
            String sortBy, String sortOrder) {
        List<Expense> expenses = expenseRepository.findAll();

        // Filtre par route
        if (tripId != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getTrip() != null && e.getTrip().getId().equals(tripId))
                    .collect(Collectors.toList());
        }

        // Filtre par véhicule
        if (vehicleId != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getVehicle() != null && e.getVehicle().getId().equals(vehicleId))
                    .collect(Collectors.toList());
        }

        // Filtre par type
        if (typeId != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getType() != null && e.getType().getId().equals(typeId))
                    .collect(Collectors.toList());
        }

        // Filtrage par statut
        if (status != null && !status.isEmpty()) {
            expenses = expenses.stream()
                    .filter(e -> e.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        // Tri
        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Expense> comparator = getComparator(sortBy);
            if (comparator != null) {
                if ("desc".equalsIgnoreCase(sortOrder)) {
                    comparator = comparator.reversed();
                }
                expenses = expenses.stream()
                        .sorted(comparator)
                        .collect(Collectors.toList());
            }
        }

        return expenses;
    }

    private Comparator<Expense> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "expense_date" -> Comparator.comparing(Expense::getExpenseDate);
            case "description" -> Comparator.comparing(Expense::getDescription);
            case "status" -> Comparator.comparing(Expense::getStatus);
            case "amount" -> Comparator.comparing(Expense::getAmount);
            case "type" -> Comparator.comparing(e -> e.getType() != null ? e.getType().getTypeName() : "");
            case "vehicle" -> Comparator.comparing(e -> e.getVehicle() != null ? e.getVehicle().getPlateNumber() : "");
            case "trip" -> Comparator.comparing(e -> {
                if (e.getTrip() != null && e.getTrip().getId() != null) {
                    return e.getTrip().getId();
                }
                return 0;
            });
            default -> null;
        };
    }

    public Optional<Expense> findById(Integer id) {
        return expenseRepository.findById(id);
    }

    public Expense save(Expense expense) {
        // Resolve entity references from IDs
        if (expense.getType() != null && expense.getType().getId() != null) {
            expense.setType(expenseTypeService.findById(expense.getType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Type de dépense non trouvé")));
        }
        
        if (expense.getTrip() != null && expense.getTrip().getId() != null) {
            expense.setTrip(tripService.findById(expense.getTrip().getId()).orElse(null));
        }
        
        if (expense.getVehicle() != null && expense.getVehicle().getId() != null) {
            expense.setVehicle(vehicleService.findById(expense.getVehicle().getId()).orElse(null));
        }
        
        return expenseRepository.save(expense);
    }

    public void deleteById(Integer id) {
        expenseRepository.deleteById(id);
    }

    public List<String> getAllStatuses() {
        return Arrays.stream(Expense.ExpenseStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());
    }
}
