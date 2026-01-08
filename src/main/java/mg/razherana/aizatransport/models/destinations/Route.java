package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "routes", schema = "public")
@Data
public class Route extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "departure_destination", nullable = false)
  private Destination departureDestination;

  @ManyToOne
  @JoinColumn(name = "arrival_destination", nullable = false)
  private Destination arrivalDestination;

  @Column(name = "distance_km", precision = 6, scale = 2)
  private BigDecimal distanceKm;

  @Column(name = "active")
  private Boolean active = true;

  @OneToMany(mappedBy = "route")
  private List<RoutePrice> prices;
}
