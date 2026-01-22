package mg.razherana.aizatransport.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Ticket;
import mg.razherana.aizatransport.repositories.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {

  private final TicketRepository ticketRepository;

  public List<Ticket> findAll() {
    return ticketRepository.findAll();
  }

  public List<Ticket> findAllFiltered(String passengerName, String sortBy, String sortOrder) {
    List<Ticket> tickets = ticketRepository.findAll();

    // Filtrage par nom de client
    if (passengerName != null && !passengerName.isEmpty()) {
      tickets = tickets.stream()
          .filter(t -> t.getClient() != null && 
                       t.getClient().getFullName().toLowerCase().contains(passengerName.toLowerCase()))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Ticket> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        tickets = tickets.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return tickets;
  }

  private Comparator<Ticket> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "purchasedate" -> Comparator.comparing(Ticket::getPurchaseDate);
      case "amount" -> Comparator.comparing(Ticket::getAmount);
      case "client" -> Comparator.comparing(t -> t.getClient() != null ? t.getClient().getFullName() : "");
      default -> null;
    };
  }

  public Optional<Ticket> findById(Integer id) {
    return ticketRepository.findById(id);
  }

  public Ticket save(Ticket ticket) {
    if (ticket.getPurchaseDate() == null) {
      ticket.setPurchaseDate(LocalDateTime.now());
    }
    return ticketRepository.save(ticket);
  }

  public void deleteById(Integer id) {
    ticketRepository.deleteById(id);
  }
}
