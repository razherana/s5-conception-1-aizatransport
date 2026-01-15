package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "route_prices", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class RoutePrice extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @ManyToOne
  @JoinColumn(name = "trip_type_id", nullable = false)
  private TripType tripType;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "effective_date", nullable = false)
  private LocalDate effectiveDate;

}
