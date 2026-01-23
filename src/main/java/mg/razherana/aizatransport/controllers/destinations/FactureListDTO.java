package mg.razherana.aizatransport.controllers.destinations;

import lombok.Data;
import mg.razherana.aizatransport.models.destinations.Facture;

@Data
public class FactureListDTO {
  private Facture facture;
  private Double totalAmount;
  private Double amountPaid;
  private Double amountRemaining;
  private boolean fullyPaid;
}
