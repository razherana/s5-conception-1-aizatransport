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
import mg.razherana.aizatransport.models.destinations.Client;
import mg.razherana.aizatransport.services.ClientService;
import mg.razherana.aizatransport.services.ClientTypeService;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;
  private final ClientTypeService clientTypeService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) Integer clientTypeId,
      @RequestParam(defaultValue = "fullName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    model.addAttribute("clients", clientService.findAllFiltered(fullName, clientTypeId, sortBy, sortOrder));
    model.addAttribute("clientTypes", clientTypeService.findAll());
    model.addAttribute("selectedFullName", fullName);
    model.addAttribute("selectedClientTypeId", clientTypeId);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/clients/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("client", new Client());
    model.addAttribute("clientTypes", clientTypeService.findAll());
    return "pages/destinations/clients/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Client client,
      @RequestParam(required = false) String target,
      RedirectAttributes redirectAttributes) {

    // Auto-assign "Passenger" client type if no client type is specified
    if (client.getClientType() == null) {
      client.setClientType(clientTypeService.findAll().stream()
          .filter(ct -> "Passager".equalsIgnoreCase(ct.getName()) || "Passenger".equalsIgnoreCase(ct.getName()))
          .findFirst()
          .orElse(null));
    }

    clientService.save(client);
    redirectAttributes.addFlashAttribute("success", "Client créé avec succès!");
    
    if (target != null && !target.isBlank()) {
      return "redirect:/clients/select?target=" + target;
    }

    return "redirect:/clients";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return clientService.findById(id)
        .map(client -> {
          model.addAttribute("client", client);
          model.addAttribute("clientTypes", clientTypeService.findAll());
          return "pages/destinations/clients/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Client non trouvé!");
          return "redirect:/clients";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute Client client,
      RedirectAttributes redirectAttributes) {

    client.setId(id);
    clientService.save(client);
    redirectAttributes.addFlashAttribute("success", "Client modifié avec succès!");
    return "redirect:/clients";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    clientService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Client supprimé avec succès!");
    return "redirect:/clients";
  }

  @GetMapping("/select")
  public String select(
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) Integer clientTypeId,
      @RequestParam(defaultValue = "fullName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      @RequestParam(required = false) String target,
      Model model) {

    model.addAttribute("clients", clientService.findAllFiltered(fullName, clientTypeId, sortBy, sortOrder).stream()
        .filter(c -> c.getClientType().getName().equalsIgnoreCase("passager"))
        .toList()
  );
    model.addAttribute("selectedFullName", fullName);
    model.addAttribute("selectedClientTypeId", clientTypeId);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("target", target);
    model.addAttribute("clientTypes", clientTypeService.findAll());

    return "pages/destinations/clients/select";
  }
}
