package mg.razherana.aizatransport.controllers.destinations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Diffusion;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffusionDetailDTO {
  private Diffusion diffusion;
  private Double amountPaid;
  private Double amountRemaining;
  private boolean fullyPaid;
}
