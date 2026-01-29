package mg.razherana.aizatransport.controllers.destinations;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureExtraListDTO {
  private Integer id;
  private Integer clientId;
  private String clientFullName;
  private LocalDate date;
  private Double total;
}
