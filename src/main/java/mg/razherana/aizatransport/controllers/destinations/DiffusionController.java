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
import mg.razherana.aizatransport.models.destinations.Diffusion;
import mg.razherana.aizatransport.services.ClientService;
import mg.razherana.aizatransport.services.DiffusionService;
import mg.razherana.aizatransport.services.TripService;

@Controller
@RequestMapping("/diffusions")
@RequiredArgsConstructor
public class DiffusionController {

  private final DiffusionService diffusionService;
  private final ClientService clientService;
  private final TripService tripService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String clientName,
      @RequestParam(defaultValue = "amount") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    List<Diffusion> diffusions = diffusionService.findAllFiltered(clientName, sortBy, sortOrder);
    
    model.addAttribute("diffusions", diffusions);
    model.addAttribute("selectedClientName", clientName);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/diffusions/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("diffusion", new Diffusion());
    model.addAttribute("clients", clientService.findAll().stream()
        .filter(c -> c.getClientType().getName().equals("Société"))
        .toList());
    model.addAttribute("trips", tripService.findAll());
    return "pages/destinations/diffusions/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Diffusion diffusion,
      @RequestParam(defaultValue = "1") Integer quantity,
      RedirectAttributes redirectAttributes) {
    
    // Create multiple diffusions based on quantity
    for (int i = 0; i < quantity; i++) {
      Diffusion newDiffusion = new Diffusion();
      newDiffusion.setDesignation(diffusion.getDesignation());
      newDiffusion.setClient(diffusion.getClient());
      newDiffusion.setTrip(diffusion.getTrip());
      newDiffusion.setAmount(diffusion.getAmount());
      diffusionService.save(newDiffusion);
    }
    
    String message = quantity > 1 
        ? quantity + " diffusions créées avec succès!" 
        : "Diffusion créée avec succès!";
    redirectAttributes.addFlashAttribute("success", message);
    return "redirect:/diffusions";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return diffusionService.findById(id)
        .map(diffusion -> {
          model.addAttribute("diffusion", diffusion);
          model.addAttribute("clients", clientService.findAll());
          model.addAttribute("trips", tripService.findAll());
          return "pages/destinations/diffusions/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Diffusion non trouvée!");
          return "redirect:/diffusions";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Diffusion diffusion,
      RedirectAttributes redirectAttributes) {
    diffusion.setId(id);
    diffusionService.save(diffusion);
    redirectAttributes.addFlashAttribute("success", "Diffusion modifiée avec succès!");
    return "redirect:/diffusions";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    diffusionService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Diffusion supprimée avec succès!");
    return "redirect:/diffusions";
  }
  
  @GetMapping("/view/{id}")
  public String view(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return diffusionService.findById(id)
        .map(diffusion -> {
          model.addAttribute("diffusion", diffusion);
          return "pages/destinations/diffusions/view";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Diffusion non trouvée!");
          return "redirect:/diffusions";
        });
  }

  @GetMapping("/client/{clientId}")
  public String clientDetails(@PathVariable Integer clientId, Model model, RedirectAttributes redirectAttributes) {
    return clientService.findById(clientId)
        .map(client -> {
          List<Diffusion> diffusions = diffusionService.findAllByClientId(clientId);
          
          Double totalAmount = diffusions.stream()
              .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0.0)
              .sum();
          
          model.addAttribute("clientId", clientId);
          model.addAttribute("clientName", client.getFullName());
          model.addAttribute("diffusions", diffusions);
          model.addAttribute("diffusionCount", diffusions.size());
          model.addAttribute("totalAmount", totalAmount);
          model.addAttribute("totalPaid", 0.0);
          model.addAttribute("totalRemaining", totalAmount);
          
          return "pages/destinations/diffusions/client-details";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Client non trouvé!");
          return "redirect:/diffusions";
        });
  }
}
