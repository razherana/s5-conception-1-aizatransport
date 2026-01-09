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
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.services.ReservationService;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String passengerName,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "reservationDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("reservations", reservationService.findAllFiltered(passengerName, status, sortBy, sortOrder));
    model.addAttribute("statuses", reservationService.getAllStatuses());
    model.addAttribute("selectedPassengerName", passengerName);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/reservations/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("reservation", new Reservation());
    model.addAttribute("statuses", reservationService.getAllStatuses());
    return "pages/destinations/reservations/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Reservation reservation, RedirectAttributes redirectAttributes) {
    reservationService.save(reservation);
    redirectAttributes.addFlashAttribute("success", "Réservation créée avec succès!");
    return "redirect:/reservations";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    model.addAttribute("statuses", reservationService.getAllStatuses());
    return reservationService.findById(id)
        .map(reservation -> {
          model.addAttribute("reservation", reservation);
          return "pages/destinations/reservations/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Réservation non trouvée!");
          return "redirect:/reservations";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Reservation reservation,
      RedirectAttributes redirectAttributes) {
    reservation.setId(id);
    reservationService.save(reservation);
    redirectAttributes.addFlashAttribute("success", "Réservation modifiée avec succès!");
    return "redirect:/reservations";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    reservationService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès!");
    return "redirect:/reservations";
  }
}
