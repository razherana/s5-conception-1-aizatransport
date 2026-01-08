package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "route_prices", schema = "public")
@Data
public class RoutePrice extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "effective_date", nullable = false)
  private LocalDate effectiveDate;

}
