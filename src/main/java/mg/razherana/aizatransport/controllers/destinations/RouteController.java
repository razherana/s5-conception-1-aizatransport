package mg.razherana.aizatransport.controllers.destinations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
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
import mg.razherana.aizatransport.models.destinations.Route;
import mg.razherana.aizatransport.models.destinations.RoutePrice;
import mg.razherana.aizatransport.services.DestinationService;
import mg.razherana.aizatransport.services.DiscountService;
import mg.razherana.aizatransport.services.DiscountTypeService;
import mg.razherana.aizatransport.services.RouteService;
import mg.razherana.aizatransport.services.SeatTypeService;
import mg.razherana.aizatransport.services.TripTypeService;

@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

  private final TripTypeService tripTypeService;
  private final SeatTypeService seatTypeService;
  private final RouteService routeService;
  private final DestinationService destinationService;
  private final DiscountTypeService discountTypeService;
  private final DiscountService discountService;

  @GetMapping
  public String list(
      @RequestParam(required = false) String departure,
      @RequestParam(required = false) String arrival,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortOrder,
      Model model) {

    List<Route> routes = routeService.findAllFiltered(departure, arrival, sortBy, sortOrder);

    // Add current prices to the model
    Map<Integer, BigDecimal> currentPrices = new HashMap<>();
    for (Route route : routes) {
      routeService.getCurrentPrice(route.getId()).ifPresent(price -> currentPrices.put(route.getId(), price));
    }

    model.addAttribute("routes", routes);
    model.addAttribute("currentPrices", currentPrices);
    model.addAttribute("selectedDeparture", departure);
    model.addAttribute("selectedArrival", arrival);
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("sortOrder", sortOrder);

    return "pages/destinations/routes/list";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("route", new Route());
    model.addAttribute("destinations", destinationService.findAll());
    model.addAttribute("seatTypes", seatTypeService.findAll());
    
    // Find Classique trip type ID
    tripTypeService.findAll().stream()
        .filter(tt -> "Classique".equals(tt.getName()))
        .findFirst()
        .ifPresent(tt -> model.addAttribute("classiqueId", tt.getId()));
    
    return "pages/destinations/routes/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Route route,
      @RequestParam Integer tripTypeId,
      @RequestParam List<Integer> seatTypeIds,
      @RequestParam List<BigDecimal> seatTypePrices,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
      RedirectAttributes redirectAttributes) {

    // Validate that we have prices for each seat type
    if (seatTypeIds.isEmpty() || seatTypeIds.size() != seatTypePrices.size()) {
      redirectAttributes.addFlashAttribute("error", "Veuillez configurer au moins un prix pour un type de siège.");
      return "redirect:/routes/create";
    }

    Route savedRoute = routeService.save(route);

    // Add prices for each seat type
    for (int i = 0; i < seatTypeIds.size(); i++) {
      routeService.addPrice(savedRoute.getId(), tripTypeId, seatTypeIds.get(i), seatTypePrices.get(i), effectiveDate);
    }

    redirectAttributes.addFlashAttribute("success", "Route créée avec succès avec " + seatTypeIds.size() + " prix configuré(s)!");
    return "redirect:/routes";
  }

  @GetMapping("/update/{id}")
  public String updateForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return routeService.findById(id)
        .map(route -> {
          model.addAttribute("route", route);
          model.addAttribute("destinations", destinationService.findAll());
          return "pages/destinations/routes/update";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Route non trouvée!");
          return "redirect:/routes";
        });
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable Integer id, @ModelAttribute Route route, RedirectAttributes redirectAttributes) {
    route.setId(id);
    routeService.save(route);
    redirectAttributes.addFlashAttribute("success", "Route modifiée avec succès!");
    return "redirect:/routes";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    routeService.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Route supprimée avec succès!");
    return "redirect:/routes";
  }

  @GetMapping("/{id}/price-history")
  public String priceHistory(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return routeService.findById(id)
        .map(route -> {
          List<RoutePrice> priceHistory = routeService.getPriceHistory(id);
          BigDecimal currentPrice = routeService.getCurrentPrice(id).orElse(BigDecimal.ZERO);

          // TODO: Refactor this logic into a service method
          // TODO: Fix "Classique always there and always on top" issue 

          // Group prices by trip type and sort each group by date
          Map<Integer, List<RoutePrice>> pricesByTripType = new java.util.HashMap<>();
          for (RoutePrice price : priceHistory) {
            Integer tripTypeId = price.getTripType().getId();
            pricesByTripType.computeIfAbsent(tripTypeId, k -> new java.util.ArrayList<>()).add(price);
          }

          // Sort each trip type group by effective date (descending for display)
          pricesByTripType.values().forEach(prices -> 
            prices.sort((p1, p2) -> p2.getEffectiveDate().compareTo(p1.getEffectiveDate()))
          );

          // Flatten back to a single list and calculate variations within each trip type
          List<PriceHistoryDTO> priceHistoryDTOs = new java.util.ArrayList<>();
          for (List<RoutePrice> tripTypePrices : pricesByTripType.values()) {
            for (int i = 0; i < tripTypePrices.size(); i++) {
              RoutePrice price = tripTypePrices.get(i);
              BigDecimal variation = null;
              String variationType = "initial";

              if (i > 0) {
                // Compare with the previous price within the SAME trip type
                BigDecimal previousPrice = tripTypePrices.get(i - 1).getPrice();
                variation = price.getPrice().subtract(previousPrice);

                if (variation.compareTo(BigDecimal.ZERO) > 0) {
                  variationType = "increase";
                } else if (variation.compareTo(BigDecimal.ZERO) < 0) {
                  variationType = "decrease";
                } else {
                  variationType = "none";
                }
              }

              priceHistoryDTOs.add(new PriceHistoryDTO(
                  price.getTripType().getId(),
                  price.getTripType().getActive(),
                  price.getTripType().getName(),
                  price.getSeatType().getId(),
                  price.getSeatType().getName(),
                  price.getSeatType().getColor(),
                  price.getEffectiveDate(),
                  price.getPrice(),
                  variation,
                  variationType,
                  i == 0 && priceHistory.size() > 0 && priceHistory.get(0).getId().equals(price.getId())));
            }
          }

          // Sort the final list by date (descending) for display
          priceHistoryDTOs.sort((p1, p2) -> p2.getEffectiveDate().compareTo(p1.getEffectiveDate()));

          // Prepare chart data (sorted chronologically by date, grouped by trip type)
          List<RoutePrice> chronologicalHistory = new java.util.ArrayList<>(priceHistory);
          chronologicalHistory.sort((p1, p2) -> p1.getEffectiveDate().compareTo(p2.getEffectiveDate()));

          List<String> chartLabels = chronologicalHistory.stream()
              .map(p -> {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd/MM/yyyy");
                return p.getEffectiveDate().format(formatter);
              })
              .collect(java.util.stream.Collectors.toList());

          List<BigDecimal> chartPrices = chronologicalHistory.stream()
              .map(RoutePrice::getPrice)
              .collect(java.util.stream.Collectors.toList());

          List<Integer> chartTripTypeIds = chronologicalHistory.stream()
              .map(p -> p.getTripType().getId())
              .collect(java.util.stream.Collectors.toList());

          List<Integer> chartSeatTypeIds = chronologicalHistory.stream()
              .map(p -> p.getSeatType().getId())
              .collect(java.util.stream.Collectors.toList());

          PriceChartDTO chartData = new PriceChartDTO(chartLabels, chartPrices, chartTripTypeIds, chartSeatTypeIds);

          // Create a map of current prices by trip type + seat type combination
          Map<String, BigDecimal> pricesByCombo = new java.util.HashMap<>();
          for (RoutePrice price : priceHistory) {
            String key = price.getTripType().getId() + "_" + price.getSeatType().getId();
            // Only keep the most recent price for each combination
            if (!pricesByCombo.containsKey(key) || 
                price.getEffectiveDate().isAfter(
                  priceHistory.stream()
                    .filter(p -> (p.getTripType().getId() + "_" + p.getSeatType().getId()).equals(key))
                    .filter(p -> pricesByCombo.containsKey(key))
                    .findFirst()
                    .map(RoutePrice::getEffectiveDate)
                    .orElse(price.getEffectiveDate())
                )) {
              pricesByCombo.put(key, price.getPrice());
            }
          }
          
          // Get most recent price for each combo (simplified approach)
          pricesByCombo.clear();
          Map<String, RoutePrice> latestPricesByCombo = new java.util.HashMap<>();
          for (RoutePrice price : priceHistory) {
            String key = price.getTripType().getId() + "_" + price.getSeatType().getId();
            RoutePrice existing = latestPricesByCombo.get(key);
            if (existing == null || price.getEffectiveDate().isAfter(existing.getEffectiveDate())) {
              latestPricesByCombo.put(key, price);
            }
          }
          for (Map.Entry<String, RoutePrice> entry : latestPricesByCombo.entrySet()) {
            pricesByCombo.put(entry.getKey(), entry.getValue().getPrice());
          }

          model.addAttribute("route", route);
          model.addAttribute("priceHistoryDTOs", priceHistoryDTOs);
          model.addAttribute("currentPrice", currentPrice);
          model.addAttribute("chartData", chartData);
          model.addAttribute("pricesByCombo", pricesByCombo);
          model.addAttribute("tripTypes", tripTypeService.findAll());
          model.addAttribute("seatTypes", seatTypeService.findAll());
          model.addAttribute("discountTypes", discountTypeService.findAll());
          model.addAttribute("discounts", discountService.findByRouteId(id));
          
          // Find Classique trip type ID for chart filtering
          tripTypeService.findAll().stream()
              .filter(tt -> "Classique".equals(tt.getName()))
              .findFirst()
              .ifPresent(tt -> model.addAttribute("classiqueId", tt.getId()));

          return "pages/destinations/routes/price-history";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Route non trouvée!");
          return "redirect:/routes";
        });
  }

  @PostMapping("/{id}/add-discount")
  public String addDiscount(
      @PathVariable Integer id,
      @RequestParam Integer tripTypeId,
      @RequestParam Integer seatTypeId,
      @RequestParam Integer discountTypeId,
      @RequestParam Double amount,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
      RedirectAttributes redirectAttributes) {

    try {
      routeService.addDiscount(id, tripTypeId, seatTypeId, discountTypeId, amount, effectiveDate);
      redirectAttributes.addFlashAttribute("success", "Réduction ajoutée avec succès!");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout de la réduction: " + e.getMessage());
    }

    return "redirect:/routes/" + id + "/price-history";
  }

  @PostMapping("/{id}/add-price")
  public String addPrice(
      @PathVariable Integer id,
      @RequestParam Integer tripTypeId,
      @RequestParam Integer seatTypeId,
      @RequestParam BigDecimal price,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
      RedirectAttributes redirectAttributes) {

    routeService.addPrice(id, tripTypeId, seatTypeId, price, effectiveDate);
    redirectAttributes.addFlashAttribute("success", "Prix ajouté avec succès!");
    return "redirect:/routes/" + id + "/price-history";
  }

  @GetMapping("/{id}/statistics")
  public String statistics(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
    return routeService.findById(id)
        .map(route -> {
          // Get statistics data
          RouteStatisticsDTO stats = routeService.getRouteStatistics(id);

          model.addAttribute("route", route);
          model.addAttribute("statistics", stats);

          return "pages/destinations/routes/statistics";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Route non trouvée!");
          return "redirect:/routes";
        });
  }
}
