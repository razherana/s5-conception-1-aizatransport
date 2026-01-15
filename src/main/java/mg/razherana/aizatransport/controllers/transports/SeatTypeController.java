package mg.razherana.aizatransport.controllers.transports;

import java.util.List;

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
import mg.razherana.aizatransport.models.transports.SeatType;
import mg.razherana.aizatransport.services.SeatTypeService;

@Controller
@RequestMapping("/seat-types")
@RequiredArgsConstructor
public class SeatTypeController {

  private final SeatTypeService seatTypeService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean active,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    List<SeatType> seatTypes = seatTypeService.findAllFiltered(
        name, sortBy, sortOrder);

    model.addAttribute("seatTypes", seatTypes);
    model.addAttribute("selectedName", name);
    model.addAttribute("selectedActive", active);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/transports/seat-types/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("seatType", new SeatType());
    return "pages/transports/seat-types/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute SeatType seatType,
      @RequestParam(required = false) String target,
      RedirectAttributes redirectAttributes) {

    seatTypeService.save(seatType);
    redirectAttributes.addFlashAttribute("success", "Type de siège créé avec succès!");

    if (target != null && !target.isBlank()) {
      return "redirect:/seat-types/select?target=" + target;
    }

    return "redirect:/seat-types";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return seatTypeService.findById(id)
        .map(seatType -> {
          model.addAttribute("seatType", seatType);
          return "pages/transports/seat-types/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Type de siège non trouvé!");
          return "redirect:/seat-types";
        });
  }

  @PostMapping("/update/{id}")
  public String update(
      @PathVariable Integer id,
      @ModelAttribute SeatType seatType,
      RedirectAttributes redirectAttributes) {

    seatType.setId(id);
    seatTypeService.save(seatType);
    redirectAttributes.addFlashAttribute("success", "Type de siège modifié avec succès!");
    return "redirect:/seat-types";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    seatTypeService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Type de siège supprimé avec succès!");
    return "redirect:/seat-types";
  }

  @GetMapping("/select")
  public String select(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      @RequestParam(required = false) String target,
      Model model) {

    List<SeatType> seatTypes = seatTypeService.findAll();

    model.addAttribute("seatTypes", seatTypes);
    model.addAttribute("selectedName", name);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("target", target);

    return "pages/transports/seat-types/select";
  }
}
