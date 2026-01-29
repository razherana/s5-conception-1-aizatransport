package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.FactureExtra;
import mg.razherana.aizatransport.models.destinations.FactureExtraFille;
import mg.razherana.aizatransport.services.ClientService;
import mg.razherana.aizatransport.services.FactureExtraService;
import mg.razherana.aizatransport.services.ProduitsExtraService;

@Controller
@RequestMapping("/factures-extras")
@RequiredArgsConstructor
public class FactureExtraController {

  private final FactureExtraService factureExtraService;
  private final ClientService clientService;
  private final ProduitsExtraService produitsExtraService;

  @GetMapping
  public String list(
      @RequestParam(required = false) Integer clientId,
      @RequestParam(required = false) LocalDate dateDebut,
      @RequestParam(required = false) LocalDate dateFin,
      @RequestParam(defaultValue = "date") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("factures", factureExtraService.findAllFiltered(clientId, dateDebut, dateFin, sortBy, sortOrder));
    model.addAttribute("clients", clientService.findAll());
    model.addAttribute("selectedClientId", clientId);
    model.addAttribute("selectedDateDebut", dateDebut);
    model.addAttribute("selectedDateFin", dateFin);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/factures-extras/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    FactureExtra facture = new FactureExtra();
    facture.setDate(LocalDate.now());
    model.addAttribute("facture", facture);
    model.addAttribute("clients", clientService.findAll());
    return "pages/destinations/factures-extras/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute FactureExtra facture,
      RedirectAttributes redirectAttributes) {

    if (facture.getDate() == null) {
      facture.setDate(LocalDate.now());
    }

    FactureExtra savedFacture = factureExtraService.save(facture);
    redirectAttributes.addFlashAttribute("success", "Facture extra créée avec succès!");
    return "redirect:/factures-extras/" + savedFacture.getId();
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureExtraService.findById(id)
        .map(facture -> {
          model.addAttribute("facture", facture);
          model.addAttribute("clients", clientService.findAll());
          return "pages/destinations/factures-extras/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture extra non trouvée!");
          return "redirect:/factures-extras";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute FactureExtra facture,
      RedirectAttributes redirectAttributes) {

    facture.setId(id);
    factureExtraService.save(facture);
    redirectAttributes.addFlashAttribute("success", "Facture extra modifiée avec succès!");
    return "redirect:/factures-extras";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    try {
      factureExtraService.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Facture extra supprimée avec succès!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la facture extra!");
    }
    return "redirect:/factures-extras";
  }

  @GetMapping("/{id}")
  @Transactional
  public String details(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureExtraService.findById(id)
        .map(facture -> {
          model.addAttribute("facture", facture);
          return "pages/destinations/factures-extras/view";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture extra non trouvée!");
          return "redirect:/factures-extras";
        });
  }

  @GetMapping("/{id}/add-product")
  public String addProductForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureExtraService.findById(id)
        .map(facture -> {
          model.addAttribute("factureId", id);
          model.addAttribute("fille", new FactureExtraFille());
          model.addAttribute("produits", produitsExtraService.findAll());
          return "pages/destinations/factures-extras/add-product";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture extra non trouvée!");
          return "redirect:/factures-extras";
        });
  }
}
