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
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.services.TripService;
import mg.razherana.aizatransport.services.RouteService;
import mg.razherana.aizatransport.services.VehicleService;
import mg.razherana.aizatransport.services.DriverService;

@Controller
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;
  private final RouteService routeService;
  private final VehicleService vehicleService;
  private final DriverService driverService;

  @GetMapping
  public String list(
      @RequestParam(required = false) Integer routeId,
      @RequestParam(required = false) Integer vehicleId,
      @RequestParam(required = false) Integer driverId,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "departureDatetime") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("trips", tripService.findAllFiltered(routeId, vehicleId, driverId, status, sortBy, sortOrder));
    model.addAttribute("routes", routeService.findAll());
    model.addAttribute("vehicles", vehicleService.findAll());
    model.addAttribute("drivers", driverService.findAll());
    model.addAttribute("statuses", tripService.getAllStatuses());
    model.addAttribute("selectedRouteId", routeId);
    model.addAttribute("selectedVehicleId", vehicleId);
    model.addAttribute("selectedDriverId", driverId);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/trips/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("trip", new Trip());
    model.addAttribute("routes", routeService.findAll());
    model.addAttribute("vehicles", vehicleService.findAll());
    model.addAttribute("drivers", driverService.findAll());
    model.addAttribute("statuses", tripService.getAllStatuses());
    return "pages/destinations/trips/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Trip trip, RedirectAttributes redirectAttributes) {
    tripService.save(trip);
    redirectAttributes.addFlashAttribute("success", "Trajet créé avec succès!");
    return "redirect:/trips";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return tripService.findById(id)
        .map(trip -> {
          model.addAttribute("trip", trip);
          model.addAttribute("routes", routeService.findAll());
          model.addAttribute("vehicles", vehicleService.findAll());
          model.addAttribute("drivers", driverService.findAll());
          model.addAttribute("statuses", tripService.getAllStatuses());
          return "pages/destinations/trips/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Trajet non trouvé!");
          return "redirect:/trips";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Trip trip,
      RedirectAttributes redirectAttributes) {
    trip.setId(id);
    tripService.save(trip);
    redirectAttributes.addFlashAttribute("success", "Trajet modifié avec succès!");
    return "redirect:/trips";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    tripService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Trajet supprimé avec succès!");
    return "redirect:/trips";
  }

  @PostMapping("/{id}/depart")
  public String depart(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    return tripService.findById(id)
        .map(trip -> {
          if ("BROUILLON".equals(trip.getStatus()) || "PLANIFIE".equals(trip.getStatus())) {
            trip.setStatus("EN_COURS");
            tripService.save(trip);
            redirectAttributes.addFlashAttribute("success", "Trajet marqué comme en cours!");
          } else {
            redirectAttributes.addFlashAttribute("error", "Le trajet ne peut pas être démarré dans cet état!");
          }
          return "redirect:/trips";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Trajet non trouvé!");
          return "redirect:/trips";
        });
  }

  @PostMapping("/{id}/arrive")
  public String arrive(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    return tripService.findById(id)
        .map(trip -> {
          if ("EN_COURS".equals(trip.getStatus())) {
            trip.setStatus("TERMINE");
            tripService.save(trip);
            redirectAttributes.addFlashAttribute("success", "Trajet marqué comme terminé!");
          } else {
            redirectAttributes.addFlashAttribute("error", "Le trajet doit être en cours pour être terminé!");
          }
          return "redirect:/trips";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Trajet non trouvé!");
          return "redirect:/trips";
        });
  }
}
