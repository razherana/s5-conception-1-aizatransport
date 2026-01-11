package mg.razherana.aizatransport.controllers.destinations;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.models.destinations.Ticket;
import mg.razherana.aizatransport.models.transports.Seat;
import mg.razherana.aizatransport.services.TripService;
import mg.razherana.aizatransport.services.RouteService;
import mg.razherana.aizatransport.services.VehicleService;
import mg.razherana.aizatransport.services.DriverService;
import mg.razherana.aizatransport.services.SeatService;
import mg.razherana.aizatransport.services.ReservationService;
import mg.razherana.aizatransport.services.TicketService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;
  private final RouteService routeService;
  private final VehicleService vehicleService;
  private final DriverService driverService;
  private final SeatService seatService;
  private final ReservationService reservationService;
  private final TicketService ticketService;

  @GetMapping
  @Transactional
  public String list(
      @RequestParam(required = false) Integer routeId,
      @RequestParam(required = false) Integer vehicleId,
      @RequestParam(required = false) Integer driverId,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "departureDatetime") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    List<Trip> trips = tripService.findAllFiltered(routeId, vehicleId, driverId, status, sortBy, sortOrder, true);

    Map<Integer, Double> tripCA = new HashMap<>();

    for (Trip trip : trips) {

      Double caReservations = new HashSet<>(trip.getReservations())
          .stream()
          .filter((e) -> e.getStatus().equalsIgnoreCase(Reservation.ReservationStatus.RESERVE.name()))
          .mapToDouble(Reservation::getAmount)
          .sum();

      Double caTickets = new HashSet<>(trip.getTickets())
          .stream()
          .mapToDouble(Ticket::getAmount)
          .sum();

      Double ca = caReservations + caTickets;
      tripCA.put(trip.getId(), ca);
    }

    model.addAttribute("tripCA", tripCA);
    model.addAttribute("trips", trips);
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
  public String arrive(
      @PathVariable Integer id,
      @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime arrivalDatetime,
      RedirectAttributes redirectAttributes) {
    return tripService.findById(id)
        .map(trip -> {
          if ("EN_COURS".equals(trip.getStatus())) {
            trip.setStatus("TERMINE");
            if (arrivalDatetime != null) {
              trip.setArrivalDatetime(arrivalDatetime);
            } else {
              trip.setArrivalDatetime(java.time.LocalDateTime.now());
            }
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

  @GetMapping("/select")
  public String select(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "departureDatetime") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      @RequestParam(required = false) String target,
      Model model) {

    var trips = tripService.findAllFiltered(null, null, null, status, sortBy, sortOrder);

    // Create a map of trip display information to avoid null issues in template
    var tripDisplayMap = new java.util.HashMap<Integer, String>();
    var tripPriceMap = new java.util.HashMap<Integer, Double>();

    for (var trip : trips) {
      String display = "";
      if (trip.getRoute() != null) {
        display = trip.getRoute().getDepartureDestination() + " → " + trip.getRoute().getArrivalDestination();

        // Get current route price
        var currentPrice = routeService.getCurrentPrice(trip.getRoute().getId());
        if (currentPrice.isPresent()) {
          tripPriceMap.put(trip.getId(), currentPrice.get().doubleValue());
        } else {
          tripPriceMap.put(trip.getId(), 0.0);
        }
      }
      tripDisplayMap.put(trip.getId(), display);
    }

    model.addAttribute("trips", trips);
    model.addAttribute("tripDisplayMap", tripDisplayMap);
    model.addAttribute("tripPriceMap", tripPriceMap);
    model.addAttribute("statuses", tripService.getAllStatuses());
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("target", target);

    return "pages/destinations/trips/select";
  }

  @GetMapping("/{id}/seats")
  @Transactional(readOnly = true)
  public String manageSeats(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return tripService.findById(id)
        .map(trip -> {
          // Get seats for the vehicle
          List<Seat> seats;
          if (trip.getVehicle() != null) {
            seats = seatService.findByVehicleId(trip.getVehicle().getId());
          } else {
            seats = List.of();
          }

          // Get all reservations and tickets for this trip
          List<Reservation> reservations = reservationService.findAll().stream()
              .filter(r -> r.getTrip() != null && r.getTrip().getId().equals(id))
              .toList();

          List<Ticket> tickets = ticketService.findAll().stream()
              .filter(t -> t.getTrip() != null && t.getTrip().getId().equals(id))
              .toList();

          // Create maps of seat ID to passenger name and passenger ID
          Map<Integer, String> seatPassengerMap = new HashMap<>();
          Map<Integer, Integer> seatPassengerIds = new HashMap<>();

          for (Reservation reservation : reservations) {
            if (reservation.getSeat() != null && reservation.getPassenger() != null) {
              seatPassengerMap.put(
                  reservation.getSeat().getId(),
                  reservation.getPassenger().getFullName() + " (Rés.)");
              seatPassengerIds.put(
                  reservation.getSeat().getId(),
                  reservation.getPassenger().getId());
            }
          }

          for (Ticket ticket : tickets) {
            if (ticket.getSeat() != null && ticket.getPassenger() != null) {
              seatPassengerMap.put(
                  ticket.getSeat().getId(),
                  ticket.getPassenger().getFullName() + " (Tick.)");
              seatPassengerIds.put(
                  ticket.getSeat().getId(),
                  ticket.getPassenger().getId());
            }
          }

          model.addAttribute("trip", trip);
          model.addAttribute("seats", seats);
          model.addAttribute("seatPassengerMap", seatPassengerMap);
          model.addAttribute("seatPassengerIds", seatPassengerIds);

          return "pages/destinations/trips/seats";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Trajet non trouvé!");
          return "redirect:/trips";
        });
  }

  @PostMapping("/{id}/seats/swap")
  @Transactional
  public String swapSeats(
      @PathVariable Integer id,
      @RequestParam Integer seat1Id,
      @RequestParam Integer seat2Id,
      RedirectAttributes redirectAttributes) {

    try {
      // Find reservations for both seats
      Reservation reservation1 = reservationService.findAll().stream()
          .filter(r -> r.getTrip() != null && r.getTrip().getId().equals(id) &&
              r.getSeat() != null && r.getSeat().getId().equals(seat1Id))
          .findFirst()
          .orElse(null);

      Reservation reservation2 = reservationService.findAll().stream()
          .filter(r -> r.getTrip() != null && r.getTrip().getId().equals(id) &&
              r.getSeat() != null && r.getSeat().getId().equals(seat2Id))
          .findFirst()
          .orElse(null);

      // Find tickets for both seats
      Ticket ticket1 = ticketService.findAll().stream()
          .filter(t -> t.getTrip() != null && t.getTrip().getId().equals(id) &&
              t.getSeat() != null && t.getSeat().getId().equals(seat1Id))
          .findFirst()
          .orElse(null);

      Ticket ticket2 = ticketService.findAll().stream()
          .filter(t -> t.getTrip() != null && t.getTrip().getId().equals(id) &&
              t.getSeat() != null && t.getSeat().getId().equals(seat2Id))
          .findFirst()
          .orElse(null);

      // Get the seat objects
      Seat seat1 = seatService.findById(seat1Id)
          .orElseThrow(() -> new IllegalArgumentException("Siège 1 non trouvé"));
      Seat seat2 = seatService.findById(seat2Id)
          .orElseThrow(() -> new IllegalArgumentException("Siège 2 non trouvé"));

      // Swap seats for reservations
      if (reservation1 != null) {
        reservation1.setSeat(seat2);
        reservationService.save(reservation1);
      }
      if (reservation2 != null) {
        reservation2.setSeat(seat1);
        reservationService.save(reservation2);
      }

      // Swap seats for tickets
      if (ticket1 != null) {
        ticket1.setSeat(seat2);
        ticketService.save(ticket1);
      }
      if (ticket2 != null) {
        ticket2.setSeat(seat1);
        ticketService.save(ticket2);
      }

      redirectAttributes.addFlashAttribute("success", "Les passagers ont été échangés avec succès!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de l'échange: " + e.getMessage());
    }

    return "redirect:/trips/" + id + "/seats";
  }
}
