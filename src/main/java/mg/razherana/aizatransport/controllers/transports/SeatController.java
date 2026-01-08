package mg.razherana.aizatransport.controllers.transports;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.transports.Seat;
import mg.razherana.aizatransport.services.SeatService;
import mg.razherana.aizatransport.services.VehicleService;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;
  private final VehicleService vehicleService;

  @GetMapping("/{vehicleId}/seats")
  public String viewSeats(@PathVariable Integer vehicleId, Model model, RedirectAttributes redirectAttributes) {
    return vehicleService.findById(vehicleId)
        .map(vehicle -> {
          // Générer les sièges si pas encore créés
          seatService.generateSeatsForVehicle(vehicleId);
          
          var seats = seatService.findByVehicleId(vehicleId);
          long availableSeatsCount = seats.stream().filter(Seat::isAvailable).count();
          
          model.addAttribute("vehicle", vehicle);
          model.addAttribute("seats", seats);
          model.addAttribute("availableSeatsCount", availableSeatsCount);
          return "pages/transports/vehicles/seats";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Véhicule non trouvé!");
          return "redirect:/vehicles";
        });
  }

  @GetMapping("/seats/{id}/update")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return seatService.findById(id)
        .map(seat -> {
          model.addAttribute("seat", seat);
          return "pages/transports/vehicles/seat-update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Siège non trouvé!");
          return "redirect:/vehicles";
        });
  }

  @PostMapping("/seats/{id}/update")
  public String update(@PathVariable Integer id, @ModelAttribute Seat seat, RedirectAttributes redirectAttributes) {
    return seatService.findById(id)
        .map(existingSeat -> {
          existingSeat.setSeatNumber(seat.getSeatNumber());
          existingSeat.setAvailable(seat.isAvailable());
          seatService.save(existingSeat);
          redirectAttributes.addFlashAttribute("success", "Siège modifié avec succès!");
          return "redirect:/vehicles/" + existingSeat.getVehicle().getId() + "/seats";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Siège non trouvé!");
          return "redirect:/vehicles";
        });
  }
}
