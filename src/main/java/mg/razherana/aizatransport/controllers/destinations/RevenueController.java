package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDateTime;

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
import mg.razherana.aizatransport.models.destinations.Revenue;
import mg.razherana.aizatransport.services.DiffusionService;
import mg.razherana.aizatransport.services.ReservationService;
import mg.razherana.aizatransport.services.RevenueService;

@Controller
@RequestMapping("/revenues")
@RequiredArgsConstructor
public class RevenueController {

  private final RevenueService revenueService;
  private final ReservationService reservationService;
  private final DiffusionService diffusionService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String sourceType,
      @RequestParam(required = false) String paymentMethod,
      @RequestParam(required = false) String dateMin,
      @RequestParam(required = false) String dateMax,
      @RequestParam(defaultValue = "paymentDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    var revenues = revenueService.findAllFiltered(sourceType, paymentMethod, dateMin, dateMax, sortBy, sortOrder);
    
    // Calculate total
    Double totalAmount = revenues.stream()
        .mapToDouble(r -> r.getAmount() != null ? r.getAmount() : 0.0)
        .sum();
    
    model.addAttribute("revenues", revenues);
    model.addAttribute("totalAmount", totalAmount);
    model.addAttribute("paymentMethods", revenueService.getAllPaymentMethods());
    model.addAttribute("selectedSourceType", sourceType);
    model.addAttribute("selectedPaymentMethod", paymentMethod);
    model.addAttribute("dateMin", dateMin);
    model.addAttribute("dateMax", dateMax);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/revenues/list";
  }

  @GetMapping("/create/reservation/{reservationId}")
  public String createFromReservation(@PathVariable Integer reservationId, Model model, RedirectAttributes redirectAttributes) {
    return reservationService.findById(reservationId)
        .map(reservation -> {
          Revenue revenue = new Revenue();
          revenue.setReservation(reservation);
          revenue.setAmount(reservation.getAmount() - (reservation.getDiscount() != null ? reservation.getDiscount() : 0.0));
          revenue.setPaymentDate(LocalDateTime.now());
          
          model.addAttribute("revenue", revenue);
          model.addAttribute("paymentMethods", revenueService.getAllPaymentMethods());
          model.addAttribute("sourceType", "reservation");
          return "pages/destinations/revenues/create";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Réservation non trouvée!");
          return "redirect:/reservations";
        });
  }

  @GetMapping("/create/diffusion/{diffusionId}")
  public String createFromDiffusion(@PathVariable Integer diffusionId, Model model, RedirectAttributes redirectAttributes) {
    return diffusionService.findById(diffusionId)
        .map(diffusion -> {
          Revenue revenue = new Revenue();
          revenue.setDiffusion(diffusion);
          revenue.setAmount(diffusion.getAmount());
          revenue.setPaymentDate(LocalDateTime.now());
          
          model.addAttribute("revenue", revenue);
          model.addAttribute("paymentMethods", revenueService.getAllPaymentMethods());
          model.addAttribute("sourceType", "diffusion");
          return "pages/destinations/revenues/create";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Diffusion non trouvée!");
          return "redirect:/diffusions";
        });
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Revenue revenue, RedirectAttributes redirectAttributes) {
    // Update status of reservation or diffusion to PAYE
    if (revenue.getReservation() != null && revenue.getReservation().getId() != null) {
      // Fetch the full reservation from database before updating
      reservationService.findById(revenue.getReservation().getId()).ifPresent(reservation -> {
        reservation.setStatus("PAYE");
        reservationService.save(reservation);
      });
    } else if (revenue.getDiffusion() != null && revenue.getDiffusion().getId() != null) {
      // Fetch the full diffusion from database before updating
      diffusionService.findById(revenue.getDiffusion().getId()).ifPresent(diffusion -> {
        diffusion.setStatus("PAYE");
        diffusionService.save(diffusion);
      });
    }
    
    revenueService.save(revenue);
    redirectAttributes.addFlashAttribute("success", "Recette enregistrée avec succès!");
    return "redirect:/revenues";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return revenueService.findById(id)
        .map(revenue -> {
          model.addAttribute("revenue", revenue);
          model.addAttribute("paymentMethods", revenueService.getAllPaymentMethods());
          return "pages/destinations/revenues/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Recette non trouvée!");
          return "redirect:/revenues";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Revenue revenue,
      RedirectAttributes redirectAttributes) {
    revenue.setId(id);
    revenueService.save(revenue);
    redirectAttributes.addFlashAttribute("success", "Recette modifiée avec succès!");
    return "redirect:/revenues";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    revenueService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Recette supprimée avec succès!");
    return "redirect:/revenues";
  }
}
