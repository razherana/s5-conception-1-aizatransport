package mg.razherana.aizatransport.controllers.destinations;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.services.PassengerService;

@Controller
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

  private final PassengerService passengerService;

  @GetMapping("/select")
  public String select(
      @RequestParam(required = false) String fullName,
      @RequestParam(defaultValue = "fullName") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      @RequestParam(required = false) String target,
      Model model) {

    model.addAttribute("passengers", passengerService.findAllFiltered(fullName, sortBy, sortOrder));
    model.addAttribute("selectedFullName", fullName);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("target", target);

    return "pages/destinations/passengers/select";
  }
}
