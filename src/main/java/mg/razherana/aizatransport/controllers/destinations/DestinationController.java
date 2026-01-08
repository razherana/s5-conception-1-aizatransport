package mg.razherana.aizatransport.controllers.destinations;

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
import mg.razherana.aizatransport.models.destinations.Destination;
import mg.razherana.aizatransport.services.DestinationService;

@Controller
@RequestMapping("/destinations")
@RequiredArgsConstructor
public class DestinationController {

  private final DestinationService destinationService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    model.addAttribute("destinations", destinationService.findAllFiltered(name, sortBy, sortOrder));
    model.addAttribute("selectedName", name);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/destinations/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("destination", new Destination());
    return "pages/destinations/destinations/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Destination destination, RedirectAttributes redirectAttributes) {
    destinationService.save(destination);
    redirectAttributes.addFlashAttribute("success", "Destination créée avec succès!");
    return "redirect:/destinations";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return destinationService.findById(id)
        .map(destination -> {
          model.addAttribute("destination", destination);
          return "pages/destinations/destinations/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Destination non trouvée!");
          return "redirect:/destinations";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Destination destination,
      RedirectAttributes redirectAttributes) {
    destination.setId(id);
    destinationService.save(destination);
    redirectAttributes.addFlashAttribute("success", "Destination modifiée avec succès!");
    return "redirect:/destinations";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    destinationService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Destination supprimée avec succès!");
    return "redirect:/destinations";
  }
}
