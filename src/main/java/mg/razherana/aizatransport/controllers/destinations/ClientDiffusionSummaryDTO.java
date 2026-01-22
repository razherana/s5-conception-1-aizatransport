package mg.razherana.aizatransport.controllers.destinations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDiffusionSummaryDTO {
  private Integer clientId;
  private String clientName;
  private Integer diffusionCount;
  private Double totalAmount;
  private Double paidAmount;
  private Double remainingAmount;
}
