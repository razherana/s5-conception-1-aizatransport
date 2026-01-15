package mg.razherana.aizatransport.controllers.destinations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mg.razherana.aizatransport.services.TripTypeService;
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
import mg.razherana.aizatransport.services.RouteService;

@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

  private final TripTypeService tripTypeService;
  private final RouteService routeService;
  private final DestinationService destinationService;

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
    return "pages/destinations/routes/create";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute Route route,
      @RequestParam BigDecimal initialPrice,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
      RedirectAttributes redirectAttributes) {

    Route savedRoute = routeService.save(route);

    // Add initial price
    routeService.addPrice(savedRoute.getId(), initialPrice, effectiveDate);

    redirectAttributes.addFlashAttribute("success", "Route créée avec succès!");
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

          PriceChartDTO chartData = new PriceChartDTO(chartLabels, chartPrices);

          model.addAttribute("route", route);
          model.addAttribute("priceHistoryDTOs", priceHistoryDTOs);
          model.addAttribute("currentPrice", currentPrice);
          model.addAttribute("chartData", chartData);
          model.addAttribute("tripTypes", tripTypeService.findAll());

          return "pages/destinations/routes/price-history";
        })
        .orElseGet(() -> {
          redirectAttributes.addFlashAttribute("error", "Route non trouvée!");
          return "redirect:/routes";
        });
  }

  @PostMapping("/{id}/add-price")
  public String addPrice(
      @PathVariable Integer id,
      @RequestParam BigDecimal price,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
      RedirectAttributes redirectAttributes) {

    routeService.addPrice(id, price, effectiveDate);
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
