package mg.razherana.aizatransport.controllers.destinations;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceChartDTO {
  private List<String> labels;
  private List<BigDecimal> prices;
}
