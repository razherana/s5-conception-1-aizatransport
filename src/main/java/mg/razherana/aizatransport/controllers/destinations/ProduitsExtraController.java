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
import mg.razherana.aizatransport.models.destinations.ProduitsExtra;
import mg.razherana.aizatransport.services.ProduitsExtraService;

@Controller
@RequestMapping("/produits-extras")
@RequiredArgsConstructor
public class ProduitsExtraController {

  private final ProduitsExtraService produitsExtraService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String nom,
      @RequestParam(required = false) Double prixMin,
      @RequestParam(required = false) Double prixMax,
      @RequestParam(defaultValue = "nom") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    model.addAttribute("produits", produitsExtraService.findAllFiltered(nom, prixMin, prixMax, sortBy, sortOrder));
    model.addAttribute("selectedNom", nom);
    model.addAttribute("selectedPrixMin", prixMin);
    model.addAttribute("selectedPrixMax", prixMax);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/produits-extras/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("produit", new ProduitsExtra());
    return "pages/destinations/produits-extras/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute ProduitsExtra produit,
      RedirectAttributes redirectAttributes) {

    produitsExtraService.save(produit);
    redirectAttributes.addFlashAttribute("success", "Produit extra créé avec succès!");
    return "redirect:/produits-extras";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return produitsExtraService.findById(id)
        .map(produit -> {
          model.addAttribute("produit", produit);
          return "pages/destinations/produits-extras/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Produit extra non trouvé!");
          return "redirect:/produits-extras";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute ProduitsExtra produit,
      RedirectAttributes redirectAttributes) {

    produit.setId(id);
    produitsExtraService.save(produit);
    redirectAttributes.addFlashAttribute("success", "Produit extra modifié avec succès!");
    return "redirect:/produits-extras";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    try {
      produitsExtraService.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Produit extra supprimé avec succès!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du produit extra!");
    }
    return "redirect:/produits-extras";
  }

  @GetMapping("/{id}")
  public String details(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return produitsExtraService.findById(id)
        .map(produit -> {
          model.addAttribute("produit", produit);
          return "pages/destinations/produits-extras/details";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Produit extra non trouvé!");
          return "redirect:/produits-extras";
        });
  }
}
