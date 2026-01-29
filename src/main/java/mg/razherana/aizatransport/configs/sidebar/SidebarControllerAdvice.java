package mg.razherana.aizatransport.configs.sidebar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SidebarControllerAdvice {

  static final List<SidebarDTO> sidebarItems = new ArrayList<>();

  @ModelAttribute
  public void addCommonAttributes(Model model) {
    if (sidebarItems.isEmpty()) {
      getSidebarItems();
    }

    // Add sidebar data
    model.addAttribute("sidebarItems", sidebarItems);
  }

  private void getSidebarItems() {
    sidebarItems.clear();

    // Dashboard - single item
    SidebarDTO dashboard = new SidebarDTO(null, false, "Tableau de bord", "/", "fa-solid fa-gauge");
    sidebarItems.add(dashboard);

    // Transports Section
    SidebarDTO transports = SidebarDTO.createMenuStatic("Transports");
    transports.createItem("Véhicules", "/vehicles", "fa-solid fa-car");
    transports.createItem("Chauffeurs", "/drivers", "fa-solid fa-user-tie");
    transports.createItem("Types de sièges", "/seat-types", "fa-solid fa-chair");
    sidebarItems.add(transports);

    // Destinations Section
    SidebarDTO destinations = SidebarDTO.createMenuStatic("Destinations");
    destinations.createItem("Routes", "/routes", "fa-solid fa-route");
    destinations.createItem("Destinations", "/destinations", "fa-solid fa-map-marker-alt");
    destinations.createItem("Voyages", "/trips", "fa-solid fa-road");
    destinations.createItem("Types de voyage", "/trip-types", "fa-solid fa-star");
    destinations.createItem("Clients", "/clients", "fa-solid fa-users");
    destinations.createItem("Types de clients", "/client-types", "fa-solid fa-user-tag");
    sidebarItems.add(destinations);

    // Gestion Section
    SidebarDTO gestion = SidebarDTO.createMenuStatic("Gestion");
    var recettesMenu = gestion.createMenu("Recettes");

    recettesMenu.createItem("Recettes", "/revenues", "fa-solid fa-cash-register");
    recettesMenu.createItem("Statistiques CA par voyage", "/revenues/ca-stats", "fa-solid fa-chart-line");

    gestion.createItem("Réservations", "/reservations", "fa-solid fa-ticket");
    gestion.createItem("Diffusions", "/diffusions", "fa-solid fa-bullhorn");
    gestion.createItem("Factures", "/factures", "fa-solid fa-file-invoice");
    gestion.createItem("Tickets", "/tickets", "fa-solid fa-ticket");
    gestion.createItem("Dépenses", "/expenses", "fa-solid fa-money-bill-wave");

    gestion
        .createItem(
            "Types de dépenses",
            "/expenses-types",
            "fa-solid fa-list");

    gestion.createItem("Types de réduction", "/discount-types", "fa-solid fa-percent");

    sidebarItems.add(gestion);

    // Extras Section
    SidebarDTO extras = SidebarDTO.createMenuStatic("Extras");
    extras.createItem("Produits extras", "/produits-extras", "fa-solid fa-box");
    extras.createItem("Factures extras", "/factures-extras", "fa-solid fa-file-invoice-dollar");
    sidebarItems.add(extras);
  }
}