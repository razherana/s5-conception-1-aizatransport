package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;
import mg.razherana.aizatransport.models.transports.Driver;
import mg.razherana.aizatransport.models.transports.Vehicle;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips", schema = "public")
@Data
public class Trip extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @ManyToOne
  @JoinColumn(name = "vehicle_id", nullable = false)
  private Vehicle vehicle;

  @ManyToOne
  @JoinColumn(name = "driver_id", nullable = false)
  private Driver driver;

  @Column(name = "departure_datetime", nullable = false)
  private LocalDateTime departureDatetime;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

}
