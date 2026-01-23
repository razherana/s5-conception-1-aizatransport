package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Revenue;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.services.DiffusionService;
import mg.razherana.aizatransport.services.ReservationService;
import mg.razherana.aizatransport.services.RevenueService;
import mg.razherana.aizatransport.services.TripService;

@Controller
@RequestMapping("/revenues")
@RequiredArgsConstructor
public class RevenueController {

  private final RevenueService revenueService;
  private final ReservationService reservationService;
  private final DiffusionService diffusionService;
  private final TripService tripService;

  /**
   * DTO for trip CA statistics
   */
  @Data
  @AllArgsConstructor
  public static class TripCAStat {
    private Trip trip;
    private double caReservations;
    private double caDiffusions;
    private double caTotal;
    private double paidDiffusions;
    private double remainingDiffusions;
    private int nbDiffusions;
  }

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
    // Update status of reservation to PAYE
    if (revenue.getReservation() != null && revenue.getReservation().getId() != null) {
      // Fetch the full reservation from database before updating
      reservationService.findById(revenue.getReservation().getId()).ifPresent(reservation -> {
        reservation.setStatus("PAYE");
        reservationService.save(reservation);
      });
    }
    // Note: Diffusion no longer has status field - payment tracking moved to Facture system
    
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

  /**
   * Display CA statistics by trip for reservations and diffusions
   */
  @GetMapping("/ca-stats")
  public String caStats(
      @RequestParam(required = false) String dateMin,
      @RequestParam(required = false) String dateMax,
      Model model) {
    
    // Default date range: current month
    LocalDateTime minDate = dateMin != null && !dateMin.isEmpty() 
        ? LocalDateTime.parse(dateMin) 
        : LocalDateTime.MIN;
    
    LocalDateTime maxDate = dateMax != null && !dateMax.isEmpty() 
        ? LocalDateTime.parse(dateMax) 
        : LocalDateTime.MAX;
    
    // Get all trips
    List<Trip> allTrips = tripService.findAllWithDiffusions();
    
    // Calculate CA for each trip and build stats list
    List<TripCAStat> tripStats = new ArrayList<>();
    Map<Integer, Double> tripIdToCA = new HashMap<>();
    
    double totalReservations = 0.0;
    double totalDiffusions = 0.0;
    double totalPaidDiffusions = 0.0;
    double totalRemainingDiffusions = 0.0;
    
    for (Trip trip : allTrips) {
      double caReservations = revenueService.getCAReservations(trip, minDate, maxDate);
      double caDiffusions = revenueService.getCADiffusions(trip, minDate, maxDate);
      double caTotal = revenueService.getCATotalTrip(trip, minDate, maxDate);
      double paidDiffusions = revenueService.getPaidDiffusions(trip, minDate, maxDate);
      double remainingDiffusions = revenueService.getRemainingAmountDiffusions(trip, minDate, maxDate);
      int nbDiffusions = revenueService.getNbDiffusions(trip, minDate, maxDate);
      
      // Only include trips with CA > 0
      if (caTotal > 0) {
        tripStats.add(new TripCAStat(trip, caReservations, caDiffusions, caTotal, paidDiffusions, remainingDiffusions, nbDiffusions));
        tripIdToCA.put(trip.getId(), caTotal);
        
        totalReservations += caReservations;
        totalDiffusions += caDiffusions;
        totalPaidDiffusions += paidDiffusions;
        totalRemainingDiffusions += remainingDiffusions;
      }
    }
    
    double totalCA = totalReservations + totalDiffusions;
 
    
    // Add attributes to model
    model.addAttribute("tripStats", tripStats);
    model.addAttribute("tripIdToCA", tripIdToCA);
    model.addAttribute("totalReservations", totalReservations);
    model.addAttribute("totalDiffusions", totalDiffusions);
    model.addAttribute("totalCA", totalCA);
    model.addAttribute("totalPaidDiffusions", totalPaidDiffusions);
    model.addAttribute("totalRemainingDiffusions", totalRemainingDiffusions);
    model.addAttribute("dateMin", minDate.toString());
    model.addAttribute("dateMax", maxDate.toString());
    
    return "pages/destinations/revenues/ca-reservation-diffusion";
  }
}
