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
import mg.razherana.aizatransport.models.destinations.Ticket;
import mg.razherana.aizatransport.services.TicketService;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

  private final TicketService ticketService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String passengerName,
      @RequestParam(defaultValue = "purchaseDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("tickets", ticketService.findAllFiltered(passengerName, sortBy, sortOrder));
    model.addAttribute("selectedPassengerName", passengerName);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/tickets/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("ticket", new Ticket());
    return "pages/destinations/tickets/create";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute Ticket ticket, RedirectAttributes redirectAttributes) {
    ticketService.save(ticket);
    redirectAttributes.addFlashAttribute("success", "Ticket créé avec succès!");
    return "redirect:/tickets";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return ticketService.findById(id)
        .map(ticket -> {
          model.addAttribute("ticket", ticket);
          return "pages/destinations/tickets/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Ticket non trouvé!");
          return "redirect:/tickets";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Ticket ticket,
      RedirectAttributes redirectAttributes) {
    ticket.setId(id);
    ticketService.save(ticket);
    redirectAttributes.addFlashAttribute("success", "Ticket modifié avec succès!");
    return "redirect:/tickets";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    ticketService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Ticket supprimé avec succès!");
    return "redirect:/tickets";
  }
}
