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
import mg.razherana.aizatransport.models.destinations.ClientType;
import mg.razherana.aizatransport.services.ClientTypeService;

@Controller
@RequestMapping("/client-types")
@RequiredArgsConstructor
public class ClientTypeController {

  private final ClientTypeService clientTypeService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean active,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    List<ClientType> clientTypes = clientTypeService.findAllFiltered(
        name, active, sortBy, sortOrder);

    model.addAttribute("clientTypes", clientTypes);
    model.addAttribute("selectedName", name);
    model.addAttribute("selectedActive", active);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/client-types/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("clientType", new ClientType());
    return "pages/destinations/client-types/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute ClientType clientType,
      @RequestParam(required = false) String target,
      RedirectAttributes redirectAttributes) {

    clientTypeService.save(clientType);
    redirectAttributes.addFlashAttribute("success", "Type de client créé avec succès!");

    if (target != null && !target.isBlank()) {
      return "redirect:/client-types/select?target=" + target;
    }

    return "redirect:/client-types";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return clientTypeService.findById(id)
        .map(clientType -> {
          model.addAttribute("clientType", clientType);
          return "pages/destinations/client-types/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Type de client non trouvé!");
          return "redirect:/client-types";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute ClientType clientType,
      RedirectAttributes redirectAttributes) {

    clientType.setId(id);
    clientTypeService.save(clientType);
    redirectAttributes.addFlashAttribute("success", "Type de client modifié avec succès!");
    return "redirect:/client-types";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    clientTypeService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Type de client supprimé avec succès!");
    return "redirect:/client-types";
  }

  @GetMapping("/select")
  public String select(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean active,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      @RequestParam(required = false) String target,
      Model model) {

    List<ClientType> clientTypes = clientTypeService.findAll();

    model.addAttribute("clientTypes", clientTypes);
    model.addAttribute("selectedName", name);
    model.addAttribute("selectedActive", active);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("target", target);

    return "pages/destinations/client-types/select";
  }
}
