package mg.razherana.aizatransport.controllers.destinations;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Expense;
import mg.razherana.aizatransport.services.ExpenseService;
import mg.razherana.aizatransport.services.ExpenseTypeService;
import mg.razherana.aizatransport.services.TripService;
import mg.razherana.aizatransport.services.VehicleService;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final TripService tripService;
    private final ExpenseService expenseService;
    private final VehicleService vehicleService;
    private final ExpenseTypeService expenseTypeService;

    @GetMapping
    public String list(
        @RequestParam(required = false) Integer tripId,
        @RequestParam(required = false) Integer vehicleId,
        @RequestParam(required = false) Integer typeId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Double amount,
        @RequestParam(defaultValue = "expenseDate") String sortBy,
        @RequestParam(defaultValue = "desc") String sortOrder,
        Model model) {

        List<Expense> expenses = expenseService.findAllFiltered(tripId, vehicleId, typeId, status, sortBy, sortOrder);


        model.addAttribute("expenses", expenses);
        model.addAttribute("trips", tripService.findAll());
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("types", expenseTypeService.findAll());
        model.addAttribute("statuses", expenseService.getAllStatuses());
        model.addAttribute("selectedTripId", tripId);
        model.addAttribute("selectedVehicleId", vehicleId);
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "pages/destinations/expenses/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("trips", tripService.findAll());
        model.addAttribute("vehicles", vehicleService.findAll());
        model.addAttribute("types", expenseTypeService.findAll());
        model.addAttribute("statuses", expenseService.getAllStatuses());
        return "pages/destinations/expenses/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Expense expense, RedirectAttributes redirectAttributes) {
        try {
            expenseService.save(expense);
            redirectAttributes.addFlashAttribute("success", "Dépense insérée avec succès!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement: " + e.getMessage());
        }
        return "redirect:/expenses";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return expenseService.findById(id)
                .map(expense -> {
                    model.addAttribute("expense", expense);
                    model.addAttribute("trips", tripService.findAll());
                    model.addAttribute("vehicles", vehicleService.findAll());
                    model.addAttribute("types", expenseTypeService.findAll());
                    model.addAttribute("statuses", expenseService.getAllStatuses());
                    return "pages/destinations/expenses/update";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Dépense non trouvée!");
                    return "redirect:/expenses";
                });
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute Expense expense,
            RedirectAttributes redirectAttributes) {
        expense.setId(id);
        expenseService.save(expense);
        redirectAttributes.addFlashAttribute("success", "Dépense modifiée avec succès!");
        return "redirect:/expenses";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        expenseService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Dépense supprimée avec succès!");
        return "redirect:/expenses";
    }
}
