package mg.razherana.aizatransport.controllers.destinations;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Discount;
import mg.razherana.aizatransport.models.destinations.Passenger;
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.models.transports.Seat;
import mg.razherana.aizatransport.services.DiscountService;
import mg.razherana.aizatransport.services.PassengerService;
import mg.razherana.aizatransport.services.ReservationService;
import mg.razherana.aizatransport.services.TripService;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;
  private final DiscountService discountService;
  private final PassengerService passengerService;
  private final TripService tripService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String passengerName,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "reservationDate") String sortBy,
      @RequestParam(defaultValue = "desc") String sortOrder,
      Model model) {

    model.addAttribute("reservations", reservationService.findAllFiltered(passengerName, status, sortBy, sortOrder));
    model.addAttribute("statuses", reservationService.getAllStatuses());
    model.addAttribute("selectedPassengerName", passengerName);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/reservations/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("reservation", new Reservation());
    model.addAttribute("statuses", reservationService.getAllStatuses());
    return "pages/destinations/reservations/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Reservation reservation,
      @RequestParam(required = false) java.util.List<Integer> seatIds,
      @RequestParam(required = false) java.util.List<java.math.BigDecimal> seatPrices,
      @RequestParam(required = false) java.util.List<java.math.BigDecimal> seatDiscounts,
      RedirectAttributes redirectAttributes) {
    
    if (seatIds != null && !seatIds.isEmpty()) {
      // Create multiple reservations for multiple seats
      int count = 0;
      for (int i = 0; i < seatIds.size(); i++) {
        Reservation newReservation = new Reservation();
        newReservation.setPassenger(reservation.getPassenger());
        newReservation.setTrip(reservation.getTrip());
        
        // Set the original price (before discount)
        if (seatPrices != null && i < seatPrices.size()) {
          newReservation.setAmount(seatPrices.get(i).doubleValue());
        } else {
          newReservation.setAmount(0.0);
        }
        
        // Set the discount amount separately
        if (seatDiscounts != null && i < seatDiscounts.size()) {
          Double discountAmount = seatDiscounts.get(i).doubleValue();
          newReservation.setDiscount(discountAmount > 0 ? discountAmount : null);
        } else {
          newReservation.setDiscount(null);
        }
        
        newReservation.setStatus(reservation.getStatus());
        newReservation.setReservationDate(reservation.getReservationDate());
        
        // Set the seat
        mg.razherana.aizatransport.models.transports.Seat seat = new mg.razherana.aizatransport.models.transports.Seat();
        seat.setId(seatIds.get(i));
        newReservation.setSeat(seat);
        
        reservationService.save(newReservation);
        count++;
      }
      redirectAttributes.addFlashAttribute("success", count + " réservation(s) créée(s) avec succès!");
    } else {
      // Single reservation (legacy support)
      reservationService.save(reservation);
      redirectAttributes.addFlashAttribute("success", "Réservation créée avec succès!");
    }
    return "redirect:/reservations";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    model.addAttribute("statuses", reservationService.getAllStatuses());
    return reservationService.findById(id)
        .map(reservation -> {
          model.addAttribute("reservation", reservation);
          return "pages/destinations/reservations/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Réservation non trouvée!");
          return "redirect:/reservations";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Reservation reservation,
      RedirectAttributes redirectAttributes) {
    reservation.setId(id);
    reservationService.save(reservation);
    redirectAttributes.addFlashAttribute("success", "Réservation modifiée avec succès!");
    return "redirect:/reservations";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    reservationService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Réservation supprimée avec succès!");
    return "redirect:/reservations";
  }
  
  /**
   * REST endpoint to calculate discount for a passenger on a specific seat
   * @param passengerId The ID of the passenger
   * @param tripId The ID of the trip
   * @param seatTypeId The ID of the seat type
   * @return JSON with discount information
   */
  @GetMapping("/calculate-discount")
  @ResponseBody
  public java.util.Map<String, Object> calculateDiscount(
      @RequestParam Integer passengerId,
      @RequestParam Integer tripId,
      @RequestParam Integer seatTypeId) {
    
    java.util.Map<String, Object> result = new java.util.HashMap<>();
    
    try {
      Passenger passenger = passengerService.findById(passengerId).orElse(null);
      Trip trip = tripService.findById(tripId).orElse(null);
      
      if (passenger == null || trip == null) {
        result.put("hasDiscount", false);
        return result;
      }
      
      // Create a temporary seat with the seat type to check discount
      Seat tempSeat = new Seat();
      mg.razherana.aizatransport.models.transports.SeatType seatType = new mg.razherana.aizatransport.models.transports.SeatType();
      seatType.setId(seatTypeId);
      tempSeat.setSeatType(seatType);
      
      Discount discount = discountService.getDiscountfor(trip, seatType, passenger);
      
      if (discount != null) {
        result.put("hasDiscount", true);
        result.put("typeName", discount.getDiscountType().getName());
        result.put("passengerAge", passenger.getAge(java.time.LocalDate.now()));
        
        // Check if it's a fixed amount or percentage
        if (discount.getAmount() != null && discount.getAmount() > 0) {
          result.put("amount", discount.getAmount());
        } else if (discount.getPercentage() != null && discount.getPercentage() > 0) {
          result.put("percentage", discount.getPercentage());
        } else {
          result.put("hasDiscount", false);
        }
      } else {
        result.put("hasDiscount", false);
      }
    } catch (Exception e) {
      result.put("hasDiscount", false);
      result.put("error", e.getMessage());
    }
    
    return result;
  }
}
