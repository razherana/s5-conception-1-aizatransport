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
import mg.razherana.aizatransport.models.transports.Driver;
import mg.razherana.aizatransport.services.DriverService;

@Controller
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

  private final DriverService driverService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("drivers", driverService.findAllFiltered(fullName, status, sortBy, sortOrder));
    model.addAttribute("statuses", driverService.getAllStatuses());
    model.addAttribute("selectedFullName", fullName);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/transports/drivers/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("driver", new Driver());
    model.addAttribute("statuses", driverService.getAllStatuses());
    return "pages/transports/drivers/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Driver driver, RedirectAttributes redirectAttributes) {
    driverService.save(driver);
    redirectAttributes.addFlashAttribute("success", "Chauffeur créé avec succès!");
    return "redirect:/drivers";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    model.addAttribute("statuses", driverService.getAllStatuses());
    return driverService.findById(id)
        .map(driver -> {
          model.addAttribute("driver", driver);
          return "pages/transports/drivers/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Chauffeur non trouvé!");
          return "redirect:/drivers";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Driver driver,
      RedirectAttributes redirectAttributes) {
    driver.setId(id);
    driverService.save(driver);
    redirectAttributes.addFlashAttribute("success", "Chauffeur modifié avec succès!");
    return "redirect:/drivers";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    driverService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Chauffeur supprimé avec succès!");
    return "redirect:/drivers";
  }
}
