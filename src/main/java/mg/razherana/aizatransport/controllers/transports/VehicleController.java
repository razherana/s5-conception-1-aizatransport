package mg.razherana.aizatransport.controllers.transports;

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
import mg.razherana.aizatransport.models.transports.Vehicle;
import mg.razherana.aizatransport.services.VehicleService;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

  private final VehicleService vehicleService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("vehicles", vehicleService.findAllFiltered(brand, status, sortBy, sortOrder));
    model.addAttribute("brands", vehicleService.getAllBrands());
    model.addAttribute("statuses", vehicleService.getAllStatuses());
    model.addAttribute("selectedBrand", brand);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/transports/vehicles/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("vehicle", new Vehicle());
    model.addAttribute("statuses", vehicleService.getAllStatuses());
    return "pages/transports/vehicles/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Vehicle vehicle, RedirectAttributes redirectAttributes) {
    vehicleService.save(vehicle);
    redirectAttributes.addFlashAttribute("success", "Véhicule créé avec succès!");
    return "redirect:/vehicles";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    model.addAttribute("statuses", vehicleService.getAllStatuses());
    return vehicleService.findById(id)
        .map(vehicle -> {
          model.addAttribute("vehicle", vehicle);
          return "pages/transports/vehicles/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Véhicule non trouvé!");
          return "redirect:/vehicles";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Vehicle vehicle,
      RedirectAttributes redirectAttributes) {
    vehicle.setId(id);
    vehicleService.save(vehicle);
    redirectAttributes.addFlashAttribute("success", "Véhicule modifié avec succès!");
    return "redirect:/vehicles";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    vehicleService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Véhicule supprimé avec succès!");
    return "redirect:/vehicles";
  }
}
