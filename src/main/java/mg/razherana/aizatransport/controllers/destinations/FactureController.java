package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
import mg.razherana.aizatransport.models.destinations.Facture;
import mg.razherana.aizatransport.models.destinations.FactureDiffusionFille;
import mg.razherana.aizatransport.models.destinations.FacturePayment;
import mg.razherana.aizatransport.services.ClientService;
import mg.razherana.aizatransport.services.DiffusionService;
import mg.razherana.aizatransport.services.FactureDiffusionFilleService;
import mg.razherana.aizatransport.services.FacturePaymentService;
import mg.razherana.aizatransport.services.FactureService;

@Controller
@RequestMapping("/factures")
@RequiredArgsConstructor
public class FactureController {

  private final FactureService factureService;
  private final ClientService clientService;
  private final DiffusionService diffusionService;
  private final FactureDiffusionFilleService factureDiffusionFilleService;
  private final FacturePaymentService facturePaymentService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String clientName,
      @RequestParam(defaultValue = "facturedate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    List<Facture> factures = factureService.findAllFiltered(clientName, sortBy, sortOrder);
    
    // Create DTOs with calculated amounts
    List<FactureListDTO> factureDTOs = factures.stream()
        .map(f -> {
          FactureListDTO dto = new FactureListDTO();
          dto.setFacture(f);
          dto.setTotalAmount(factureService.calculateTotalAmount(f));
          dto.setAmountPaid(factureService.calculateAmountPaid(f));
          dto.setAmountRemaining(factureService.calculateAmountRemaining(f));
          dto.setFullyPaid(factureService.isFullyPaid(f));
          return dto;
        })
        .collect(Collectors.toList());
    
    model.addAttribute("factureDTOs", factureDTOs);
    model.addAttribute("selectedClientName", clientName);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/factures/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("facture", new Facture());
    model.addAttribute("clients", clientService.findAll().stream()
        .filter(c -> c.getClientType().getName().equals("Société"))
        .collect(Collectors.toList()));
    model.addAttribute("diffusions", diffusionService.findAll());
    return "pages/destinations/factures/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Facture facture,
      @RequestParam(required = false) List<Integer> selectedDiffusions,
      RedirectAttributes redirectAttributes) {
    
    // Set current date if not provided
    if (facture.getFactureDate() == null) {
      facture.setFactureDate(LocalDateTime.now());
    }
    
    // Save the facture first
    Facture savedFacture = factureService.save(facture);
    
    // Add selected diffusions if any
    if (selectedDiffusions != null && !selectedDiffusions.isEmpty()) {
      for (Integer diffusionId : selectedDiffusions) {
        diffusionService.findById(diffusionId).ifPresent(diffusion -> {
          FactureDiffusionFille fdf = new FactureDiffusionFille();
          fdf.setFacture(savedFacture);
          fdf.setDiffusion(diffusion);
          factureDiffusionFilleService.save(fdf);
        });
      }
    }
    
    redirectAttributes.addFlashAttribute("success", "Facture créée avec succès!");
    return "redirect:/factures";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureService.findById(id)
        .map(facture -> {
          model.addAttribute("facture", facture);
          model.addAttribute("clients", clientService.findAll());
          return "pages/destinations/factures/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture non trouvée!");
          return "redirect:/factures";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Facture facture,
      RedirectAttributes redirectAttributes) {
    facture.setId(id);
    factureService.save(facture);
    redirectAttributes.addFlashAttribute("success", "Facture modifiée avec succès!");
    return "redirect:/factures";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    factureService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Facture supprimée avec succès!");
    return "redirect:/factures";
  }

  @GetMapping("/view/{id}")
  public String view(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureService.findByIdWithDetails(id)
        .map(facture -> {
          Double totalAmount = factureService.calculateTotalAmount(facture);
          Double amountPaid = factureService.calculateAmountPaid(facture);
          Double amountRemaining = factureService.calculateAmountRemaining(facture);
          boolean isFullyPaid = factureService.isFullyPaid(facture);
          
          model.addAttribute("facture", facture);
          model.addAttribute("totalAmount", totalAmount);
          model.addAttribute("amountPaid", amountPaid);
          model.addAttribute("amountRemaining", amountRemaining);
          model.addAttribute("isFullyPaid", isFullyPaid);
          
          return "pages/destinations/factures/view";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture non trouvée!");
          return "redirect:/factures";
        });
  }

  @GetMapping("/{id}/add-diffusion")
  public String addDiffusionForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureService.findById(id)
        .map(facture -> {
          model.addAttribute("facture", facture);
          model.addAttribute("diffusions", diffusionService.findAll());
          model.addAttribute("factureDiffusionFille", new FactureDiffusionFille());
          return "pages/destinations/factures/add-diffusion";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture non trouvée!");
          return "redirect:/factures";
        });
  }

  @PostMapping("/{id}/add-diffusion")
  public String addDiffusion(
      @PathVariable Integer id,
      @RequestParam Integer diffusionId,
      RedirectAttributes redirectAttributes) {
    
    try {
      Facture facture = factureService.findById(id)
          .orElseThrow(() -> new RuntimeException("Facture non trouvée!"));
      
      FactureDiffusionFille fdf = new FactureDiffusionFille();
      fdf.setFacture(facture);
      fdf.setDiffusion(diffusionService.findById(diffusionId)
          .orElseThrow(() -> new RuntimeException("Diffusion non trouvée!")));
      
      factureDiffusionFilleService.save(fdf);
      redirectAttributes.addFlashAttribute("success", "Diffusion ajoutée à la facture!");
      return "redirect:/factures/view/" + id;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
      return "redirect:/factures/view/" + id;
    }
  }

  @GetMapping("/{id}/add-payment")
  public String addPaymentForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return factureService.findByIdWithDetails(id)
        .map(facture -> {
          Double amountRemaining = factureService.calculateAmountRemaining(facture);
          
          if (amountRemaining <= 0) {
            redirectAttributes.addFlashAttribute("error", "Cette facture est déjà entièrement payée!");
            return "redirect:/factures/view/" + id;
          }
          
          model.addAttribute("facture", facture);
          model.addAttribute("amountRemaining", amountRemaining);
          model.addAttribute("facturePayment", new FacturePayment());
          return "pages/destinations/factures/add-payment";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Facture non trouvée!");
          return "redirect:/factures";
        });
  }

  @PostMapping("/{id}/add-payment")
  public String addPayment(
      @PathVariable Integer id,
      @RequestParam Double amount,
      @RequestParam LocalDateTime paymentDate,
      RedirectAttributes redirectAttributes) {
    
    try {
      Facture facture = factureService.findByIdWithDetails(id)
          .orElseThrow(() -> new RuntimeException("Facture non trouvée!"));
      
      Double amountRemaining = factureService.calculateAmountRemaining(facture);
      
      if (amount > amountRemaining) {
        redirectAttributes.addFlashAttribute("error", 
            "Le montant du paiement (" + amount + " Ar) dépasse le reste à payer (" + 
            amountRemaining + " Ar)!");
        return "redirect:/factures/" + id + "/add-payment";
      }
      
      FacturePayment payment = new FacturePayment();
      payment.setFacture(facture);
      payment.setAmount(amount);
      payment.setPaymentDate(paymentDate);
      
      facturePaymentService.save(payment);
      redirectAttributes.addFlashAttribute("success", "Paiement enregistré avec succès!");
      return "redirect:/factures/view/" + id;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
      return "redirect:/factures/" + id + "/add-payment";
    }
  }

  @PostMapping("/{factureId}/remove-diffusion/{diffusionId}")
  public String removeDiffusion(
      @PathVariable Integer factureId,
      @PathVariable Integer diffusionId,
      RedirectAttributes redirectAttributes) {
    
    try {
      List<FactureDiffusionFille> links = factureDiffusionFilleService.findAllByDiffusionId(diffusionId);
      links.stream()
          .filter(l -> l.getFacture().getId().equals(factureId))
          .forEach(l -> factureDiffusionFilleService.deleteById(l.getId()));
      
      redirectAttributes.addFlashAttribute("success", "Diffusion retirée de la facture!");
      return "redirect:/factures/view/" + factureId;
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
      return "redirect:/factures/view/" + factureId;
    }
  }
}
