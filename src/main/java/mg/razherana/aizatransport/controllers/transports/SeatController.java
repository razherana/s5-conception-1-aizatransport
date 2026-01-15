package mg.razherana.aizatransport.controllers.transports;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.models.destinations.Ticket;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.models.transports.Seat;
import mg.razherana.aizatransport.models.transports.Vehicle;
import mg.razherana.aizatransport.services.ReservationService;
import mg.razherana.aizatransport.services.RoutePriceService;
import mg.razherana.aizatransport.services.SeatService;
import mg.razherana.aizatransport.services.SeatTypeService;
import mg.razherana.aizatransport.services.TicketService;
import mg.razherana.aizatransport.services.TripService;
import mg.razherana.aizatransport.services.VehicleService;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;
  private final VehicleService vehicleService;
  private final TripService tripService;
  private final ReservationService reservationService;
  private final TicketService ticketService;
  private final SeatTypeService seatTypeService;
  private final RoutePriceService routePriceService;

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
          model.addAttribute("seatTypes", seatTypeService.findAll());
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
          model.addAttribute("seatTypes", seatTypeService.findAll());
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
          existingSeat.setSeatType(seat.getSeatType());
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

  @GetMapping("/seats/select")
  @Transactional(readOnly = true)
  public String select(
      @RequestParam Integer tripId,
      @RequestParam(required = false) String target,
      Model model) {

    Trip trip = tripService.findById(tripId).orElseThrow(() -> {
      return new IllegalArgumentException("Trajet non trouvé pour l'ID: " + tripId);
    });

    // Force initialization of vehicle and its seats
    if (trip.getVehicle() != null) {
      trip.getVehicle().getId(); // Touch the vehicle to ensure it's loaded
    }

    // Get all reservations and tickets for this trip
    List<Reservation> reservations = reservationService.findAll().stream()
        .filter(r -> r.getTrip() != null && r.getTrip().getId().equals(tripId))
        .toList();

    List<Ticket> tickets = ticketService.findAll().stream()
        .filter(t -> t.getTrip() != null && t.getTrip().getId().equals(tripId))
        .toList();

    // Create a map of seat ID to passenger name
    Map<Integer, String> seatPassengerMap = new HashMap<>();

    for (Reservation reservation : reservations) {
      if (reservation.getSeat() != null && reservation.getPassenger() != null) {
        seatPassengerMap.put(
            reservation.getSeat().getId(),
            reservation.getPassenger().getFullName() + " (Rés.)");
      }
    }

    for (Ticket ticket : tickets) {
      if (ticket.getSeat() != null && ticket.getPassenger() != null) {
        seatPassengerMap.put(
            ticket.getSeat().getId(),
            ticket.getPassenger().getFullName() + " (Tick.)");
      }
    }

    // Get seats for the vehicle
    List<Seat> seats;
    if (trip.getVehicle() != null) {
      seats = seatService.findByVehicleId(trip.getVehicle().getId());
    } else {
      seats = List.of();
    }

    // Calculate seat prices using the service
    Map<String, BigDecimal> priceMap = routePriceService.calculatePriceMapForTrip(trip);

    // Create a seat price map (seatId -> price)
    Map<Integer, BigDecimal> seatPriceMap = new HashMap<>();
    for (Seat seat : seats) {
      if (seat.getSeatType() != null && trip.getRoute() != null && trip.getTripType() != null) {
        String key = trip.getRoute().getId() + "_" + trip.getTripType().getId() + "_"
            + seat.getSeatType().getId();
        BigDecimal price = priceMap.getOrDefault(key, BigDecimal.ZERO);
        seatPriceMap.put(seat.getId(), price);
      }
    }

    model.addAttribute("seats", seats);
    model.addAttribute("seatPassengerMap", seatPassengerMap);
    model.addAttribute("seatPriceMap", seatPriceMap);
    model.addAttribute("tripId", tripId);
    model.addAttribute("target", target);
    model.addAttribute("seatTypes", seatTypeService.findAll());

    return "pages/transports/seats/select";
  }

  @GetMapping("/seats/initialize")
  public String initializeSeats() {
    List<Vehicle> vehicles = vehicleService.findAll();
    for (Vehicle vehicle : vehicles) {
      seatService.generateSeatsForVehicle(vehicle.getId());
    }

    return "redirect:/vehicles";
  }
}
