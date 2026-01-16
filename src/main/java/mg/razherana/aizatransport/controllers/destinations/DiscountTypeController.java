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
import mg.razherana.aizatransport.models.destinations.DiscountType;
import mg.razherana.aizatransport.services.DiscountTypeService;

@Controller
@RequestMapping("/discount-types")
@RequiredArgsConstructor
public class DiscountTypeController {

    private final DiscountTypeService discountTypeService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {

        List<DiscountType> discountTypes = discountTypeService.findAllFiltered(
                name, sortBy, sortOrder
        );

        model.addAttribute("discountTypes", discountTypes);
        model.addAttribute("selectedName", name);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "pages/destinations/discount-types/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("discountType", new DiscountType());
        return "pages/destinations/discount-types/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute DiscountType discountType,
            @RequestParam(required = false) String target,
            RedirectAttributes redirectAttributes) {

        discountTypeService.save(discountType);
        redirectAttributes.addFlashAttribute("success", "Type de réduction créé avec succès!");

        if (target != null && !target.isBlank()) {
            return "redirect:/discount-types/select?target=" + target;
        }

        return "redirect:/discount-types";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return discountTypeService.findById(id)
                .map(discountType -> {
                    model.addAttribute("discountType", discountType);
                    return "pages/destinations/discount-types/update";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Type de réduction non trouvé!");
                    return "redirect:/discount-types";
                });
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @ModelAttribute DiscountType discountType,
            RedirectAttributes redirectAttributes) {

        discountType.setId(id);
        discountTypeService.save(discountType);
        redirectAttributes.addFlashAttribute("success", "Type de réduction modifié avec succès!");
        return "redirect:/discount-types";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        discountTypeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Type de réduction supprimé avec succès!");
        return "redirect:/discount-types";
    }

    @GetMapping("/select")
    public String select(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String target,
            Model model) {

        List<DiscountType> discountTypes = discountTypeService.findAll();

        model.addAttribute("discountTypes", discountTypes);
        model.addAttribute("selectedName", name);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("target", target);

        return "pages/destinations/discount-types/select";
    }
}
