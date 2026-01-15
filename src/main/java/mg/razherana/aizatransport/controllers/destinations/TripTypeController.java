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
import mg.razherana.aizatransport.models.destinations.TripType;
import mg.razherana.aizatransport.services.TripTypeService;

@Controller
@RequestMapping("/trip-types")
@RequiredArgsConstructor
public class TripTypeController {

    private final TripTypeService tripTypeService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Model model) {

        List<TripType> tripTypes = tripTypeService.findAllFiltered(
                name, active, sortBy, sortOrder
        );

        model.addAttribute("tripTypes", tripTypes);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedActive", active);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "pages/destinations/trip-types/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("tripType", new TripType());
        return "pages/destinations/trip-types/create";
    }

    @PostMapping("/create")
    public String create(
            @ModelAttribute TripType tripType,
            @RequestParam(required = false) String target,
            RedirectAttributes redirectAttributes) {

        tripTypeService.save(tripType);
        redirectAttributes.addFlashAttribute("success", "Type de voyage créé avec succès!");

        if (target != null && !target.isBlank()) {
            return "redirect:/trip-types/select?target=" + target;
        }

        return "redirect:/trip-types";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return tripTypeService.findById(id)
                .map(tripType -> {
                    model.addAttribute("tripType", tripType);
                    return "pages/destinations/trip-types/update";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Type de voyage non trouvé!");
                    return "redirect:/trip-types";
                });
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Integer id,
            @ModelAttribute TripType tripType,
            RedirectAttributes redirectAttributes) {

        tripType.setId(id);
        tripTypeService.save(tripType);
        redirectAttributes.addFlashAttribute("success", "Type de voyage modifié avec succès!");
        return "redirect:/trip-types";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        tripTypeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Type de voyage supprimé avec succès!");
        return "redirect:/trip-types";
    }

    @GetMapping("/select")
    public String select(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String target,
            Model model) {

        List<TripType> tripTypes = tripTypeService.findAll();

        model.addAttribute("tripTypes", tripTypes);
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedActive", active);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("target", target);

        return "pages/destinations/trip-types/select";
    }
}
