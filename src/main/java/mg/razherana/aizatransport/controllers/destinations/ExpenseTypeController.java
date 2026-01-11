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
import mg.razherana.aizatransport.models.destinations.ExpenseType;
import mg.razherana.aizatransport.services.ExpenseTypeService;

@Controller
@RequestMapping("/expenses-types")
@RequiredArgsConstructor
public class ExpenseTypeController {

    private final ExpenseTypeService expenseTypeService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String typeName,
            @RequestParam(defaultValue = "typeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {

        List<ExpenseType> expenseTypes = expenseTypeService.findAll();

        model.addAttribute("expenseTypes", expenseTypes);
        model.addAttribute("selectedTypeName", typeName);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "pages/destinations/expenses-types/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("expenseType", new ExpenseType());
        return "pages/destinations/expenses-types/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute ExpenseType expenseType,
            @RequestParam(required = false) String target,
            RedirectAttributes redirectAttributes) {

        expenseTypeService.save(expenseType);
        redirectAttributes.addFlashAttribute("success", "Type de dépense créé avec succès!");

        if (target != null && !target.isBlank()) {
            return "redirect:/expenses-types/select?target=" + target;
        }

        return "redirect:/expenses-types";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return expenseTypeService.findById(id)
                .map(expenseType -> {
                    model.addAttribute("expenseType", expenseType);
                    return "pages/destinations/expenses-types/update";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Type de dépense non trouvé!");
                    return "redirect:/expenses-types";
                });
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @ModelAttribute ExpenseType expenseType,
            RedirectAttributes redirectAttributes) {

        expenseType.setId(id);
        expenseTypeService.save(expenseType);
        redirectAttributes.addFlashAttribute("success", "Type de dépense modifié avec succès!");
        return "redirect:/expenses-types";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        expenseTypeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Type de dépense supprimé avec succès!");
        return "redirect:/expenses-types";
    }

    @GetMapping("/select")
    public String select(
            @RequestParam(required = false) String typeName,
            @RequestParam(defaultValue = "typeName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String target,
            Model model) {

        List<ExpenseType> expenseTypes = expenseTypeService.findAll();

        model.addAttribute("expenseTypes", expenseTypes);
        model.addAttribute("selectedTypeName", typeName);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("target", target);

        return "pages/destinations/expenses-types/select";
    }
}