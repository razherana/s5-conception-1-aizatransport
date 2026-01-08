package mg.razherana.aizatransport.controllers.destinations;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceHistoryDTO {
  private LocalDate effectiveDate;
  private BigDecimal price;
  private BigDecimal variation;
  private String variationType; // "increase", "decrease", "none", "initial"
  private boolean isCurrent;
}
