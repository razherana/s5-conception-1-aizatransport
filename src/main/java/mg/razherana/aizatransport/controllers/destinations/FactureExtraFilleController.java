package mg.razherana.aizatransport.controllers.destinations;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.FactureExtraFille;
import mg.razherana.aizatransport.services.FactureExtraFilleService;
import mg.razherana.aizatransport.services.FactureExtraService;
import mg.razherana.aizatransport.services.ProduitsExtraService;

@Controller
@RequestMapping("/factures-extras-filles")
@RequiredArgsConstructor
public class FactureExtraFilleController {

  private final FactureExtraFilleService factureExtraFilleService;
  private final FactureExtraService factureExtraService;
  private final ProduitsExtraService produitsExtraService;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("filles", factureExtraFilleService.findAll());
    return "pages/destinations/factures-extras-filles/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("fille", new FactureExtraFille());
    model.addAttribute("factures", factureExtraService.findAll());
    model.addAttribute("produits", produitsExtraService.findAll());
    return "pages/destinations/factures-extras-filles/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute FactureExtraFille fille,
      RedirectAttributes redirectAttributes) {

    // Auto-fill prix unitaire from produit if not set
    if (fille.getPrixUnitaire() == null && fille.getProduitsExtra() != null) {
      fille.setPrixUnitaire(fille.getProduitsExtra().getPrix());
    }

    factureExtraFilleService.save(fille);
    redirectAttributes.addFlashAttribute("success", "Produit ajouté avec succès!");
    
    // Redirect to facture view if facture is set
    if (fille.getFactureExtra() != null && fille.getFactureExtra().getId() != null) {
      return "redirect:/factures-extras/" + fille.getFactureExtra().getId();
    }
    return "redirect:/factures-extras-filles";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureExtraFilleService.findById(id)
        .map(fille -> {
          model.addAttribute("fille", fille);
          model.addAttribute("factures", factureExtraService.findAll());
          model.addAttribute("produits", produitsExtraService.findAll());
          return "pages/destinations/factures-extras-filles/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Ligne de facture non trouvée!");
          return "redirect:/factures-extras-filles";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute FactureExtraFille fille,
      RedirectAttributes redirectAttributes) {

    fille.setId(id);
    factureExtraFilleService.save(fille);
    redirectAttributes.addFlashAttribute("success", "Ligne de facture modifiée avec succès!");
    return "redirect:/factures-extras-filles";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    try {
      factureExtraFilleService.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Ligne de facture supprimée avec succès!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la ligne de facture!");
    }
    return "redirect:/factures-extras-filles";
  }

  @GetMapping("/{id}")
  public String details(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureExtraFilleService.findById(id)
        .map(fille -> {
          model.addAttribute("fille", fille);
          return "pages/destinations/factures-extras-filles/details";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Ligne de facture non trouvée!");
          return "redirect:/factures-extras-filles";
        });
  }
}
