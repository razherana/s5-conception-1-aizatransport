package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import mg.razherana.aizatransport.models.destinations.Diffusion;
import mg.razherana.aizatransport.models.destinations.DiffusionFille;
import mg.razherana.aizatransport.models.destinations.Revenue;
import mg.razherana.aizatransport.services.ClientService;
import mg.razherana.aizatransport.services.DiffusionFilleService;
import mg.razherana.aizatransport.services.DiffusionService;
import mg.razherana.aizatransport.services.RevenueService;
import mg.razherana.aizatransport.services.TripService;

@Controller
@RequestMapping("/diffusions")
@RequiredArgsConstructor
public class DiffusionController {

  private final DiffusionService diffusionService;
  private final ClientService clientService;
  private final TripService tripService;
  private final DiffusionFilleService diffusionFilleService;
  private final RevenueService revenueService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String clientName,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "paymentDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    List<Diffusion> diffusions = diffusionService.findAllFiltered(clientName, status, sortBy, sortOrder);
    
    // Group diffusions by client and calculate summaries
    Map<Integer, List<Diffusion>> diffusionsByClient = diffusions.stream()
        .collect(Collectors.groupingBy(d -> d.getClient().getId()));
    
    List<ClientDiffusionSummaryDTO> summaries = new ArrayList<>();
    for (Map.Entry<Integer, List<Diffusion>> entry : diffusionsByClient.entrySet()) {
      List<Diffusion> clientDiffusions = entry.getValue();
      Diffusion firstDiffusion = clientDiffusions.get(0);
      
      Double totalAmount = clientDiffusions.stream()
          .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0.0)
          .sum();
      
      Double paidAmount = clientDiffusions.stream()
          .mapToDouble(d -> diffusionService.calculateAmountPaid(d))
          .sum();
      
      Double remainingAmount = totalAmount - paidAmount;
      
      summaries.add(new ClientDiffusionSummaryDTO(
          firstDiffusion.getClient().getId(),
          firstDiffusion.getClient().getFullName(),
          clientDiffusions.size(),
          totalAmount,
          paidAmount,
          remainingAmount
      ));
    }
    
    model.addAttribute("clientDiffusionSummaries", summaries);
    model.addAttribute("statuses", diffusionService.getAllStatuses());
    model.addAttribute("selectedClientName", clientName);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/diffusions/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("diffusion", new Diffusion());
    model.addAttribute("clients", clientService.findAll().stream()
        .filter(c -> c.getClientType().getName().equals("Société"))
        .toList()
);
    model.addAttribute("trips", tripService.findAll());
    model.addAttribute("statuses", diffusionService.getAllStatuses());
    return "pages/destinations/diffusions/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Diffusion diffusion,
      @RequestParam(defaultValue = "1") Integer quantity,
      RedirectAttributes redirectAttributes) {
    
    // Create multiple diffusions based on quantitys
    for (int i = 0; i < quantity; i++) {
      Diffusion newDiffusion = new Diffusion();
      newDiffusion.setClient(diffusion.getClient());
      newDiffusion.setTrip(diffusion.getTrip());
      newDiffusion.setAmount(diffusion.getAmount());
      newDiffusion.setPaymentDate(diffusion.getPaymentDate());
      newDiffusion.setStatus(diffusion.getStatus());
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
          model.addAttribute("statuses", diffusionService.getAllStatuses());
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
          
          // Create DTOs with calculated values
          List<DiffusionDetailDTO> diffusionDetails = diffusions.stream()
              .map(d -> new DiffusionDetailDTO(
                  d,
                  diffusionService.calculateAmountPaid(d),
                  diffusionService.calculateAmountRemaining(d),
                  diffusionService.isFullyPaid(d)
              ))
              .collect(Collectors.toList());
          
          Double totalAmount = diffusions.stream()
              .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0.0)
              .sum();
          
          Double totalPaid = diffusions.stream()
              .mapToDouble(d -> diffusionService.calculateAmountPaid(d))
              .sum();
          
          Double totalRemaining = totalAmount - totalPaid;
          
          // Get all payments for this client's diffusions
          List<DiffusionFille> payments = diffusions.stream()
              .flatMap(d -> d.getDiffusionFilles().stream())
              .sorted((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()))
              .collect(Collectors.toList());
          
          model.addAttribute("clientId", clientId);
          model.addAttribute("clientName", client.getFullName());
          model.addAttribute("diffusionDetails", diffusionDetails);
          model.addAttribute("diffusionCount", diffusions.size());
          model.addAttribute("totalAmount", totalAmount);
          model.addAttribute("totalPaid", totalPaid);
          model.addAttribute("totalRemaining", totalRemaining);
          model.addAttribute("payments", payments);
          
          return "pages/destinations/diffusions/client-details";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Client non trouvé!");
          return "redirect:/diffusions";
        });
  }

  @GetMapping("/client/{clientId}/payment")
  public String paymentForm(@PathVariable Integer clientId, Model model, RedirectAttributes redirectAttributes) {
    return clientService.findById(clientId)
        .map(client -> {
          List<Diffusion> diffusions = diffusionService.findAllByClientId(clientId);
          
          // Filter diffusions that still have remaining amounts
          List<Diffusion> unpaidDiffusions = diffusions.stream()
              .filter(d -> diffusionService.calculateAmountRemaining(d) > 0)
              .collect(Collectors.toList());
          
          if (unpaidDiffusions.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Toutes les diffusions sont payées!");
            return "redirect:/diffusions/client/" + clientId;
          }
          
          // Create DTOs with calculated values
          List<DiffusionDetailDTO> diffusionDetails = unpaidDiffusions.stream()
              .map(d -> new DiffusionDetailDTO(
                  d,
                  diffusionService.calculateAmountPaid(d),
                  diffusionService.calculateAmountRemaining(d),
                  diffusionService.isFullyPaid(d)
              ))
              .collect(Collectors.toList());
          
          Double totalAmount = diffusions.stream()
              .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0.0)
              .sum();
          
          Double totalPaid = diffusions.stream()
              .mapToDouble(d -> diffusionService.calculateAmountPaid(d))
              .sum();
          
          Double totalRemaining = totalAmount - totalPaid;
          
          model.addAttribute("clientId", clientId);
          model.addAttribute("clientName", client.getFullName());
          model.addAttribute("diffusionDetails", diffusionDetails);
          model.addAttribute("totalAmount", totalAmount);
          model.addAttribute("totalPaid", totalPaid);
          model.addAttribute("totalRemaining", totalRemaining);
          model.addAttribute("diffusionFille", new DiffusionFille());
          
          return "pages/destinations/diffusions/payment";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Client non trouvé!");
          return "redirect:/diffusions";
        });
  }

  @PostMapping("/client/{clientId}/payment")
  public String savePayment(
      @PathVariable Integer clientId,
      @RequestParam("diffusion.id") Integer diffusionId,
      @RequestParam Double amount,
      @RequestParam LocalDateTime paymentDate,
      @RequestParam String paymentMethod,
      @RequestParam(required = false) String reference,
      @RequestParam(required = false) String notes,
      RedirectAttributes redirectAttributes) {
    
    try {
      
      // Load the diffusion with payments
      Diffusion diffusion = diffusionService.findByIdWithPayments(diffusionId)
          .orElseThrow(() -> new RuntimeException("Diffusion non trouvée!"));
      
      // Validate that payment amount doesn't exceed remaining amount
      Double amountRemaining = diffusionService.calculateAmountRemaining(diffusion);
      System.out.println("Amount remaining: " + amountRemaining);
      
      if (amount > amountRemaining) {
        redirectAttributes.addFlashAttribute("error", 
            "Le montant du paiement (" + amount + " Ar) dépasse le reste à payer (" + 
            amountRemaining + " Ar)!");
        return "redirect:/diffusions/client/" + clientId + "/payment";
      }
      
      // Create and save the payment
      DiffusionFille diffusionFille = new DiffusionFille();
      diffusionFille.setDiffusion(diffusion);
      diffusionFille.setAmount(amount);
      diffusionFille.setPaymentDate(paymentDate);
      diffusionFille.setPaymentMethod(paymentMethod);
      diffusionFille.setReference(reference);
      diffusionFille.setNotes(notes);
      
      System.out.println("DiffusionFille created, about to save...");
      DiffusionFille saved = diffusionFilleService.save(diffusionFille);
      System.out.println("DiffusionFille saved with ID: " + saved.getId());
      
      // Create corresponding revenue
      Revenue revenue = new Revenue();
      revenue.setDiffusion(diffusion);
      revenue.setAmount(amount);
      revenue.setPaymentDate(paymentDate);
      revenue.setPaymentMethod(paymentMethod);
      revenue.setReference(reference);
      revenue.setNotes(notes);
      
      // Save the revenue first
      Revenue savedRevenue = revenueService.save(revenue);
      System.out.println("Revenue saved with ID: " + savedRevenue.getId());
      
      // Link the revenue to the DiffusionFille
      saved.setRevenue(savedRevenue);
      diffusionFilleService.save(saved);
      
      System.out.println("Revenue created and linked to DiffusionFille");
      
      redirectAttributes.addFlashAttribute("success", "Paiement enregistré avec succès!");
      return "redirect:/diffusions/client/" + clientId;
    } catch (Exception e) {
      System.err.println("=== ERROR saving payment ===");
      e.printStackTrace();
      redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement du paiement: " + e.getMessage());
      return "redirect:/diffusions/client/" + clientId + "/payment";
    }
  }
}
