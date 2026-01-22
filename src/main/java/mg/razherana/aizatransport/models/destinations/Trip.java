package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;
import mg.razherana.aizatransport.models.transports.Driver;
import mg.razherana.aizatransport.models.transports.Vehicle;


@Entity
@Table(name = "trips", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Trip extends BasicEntity {

  public enum TripStatus {
    BROUILLON,
    PLANIFIE,
    EN_COURS,
    TERMINE,
    ANNULE;
  }

  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @ManyToOne
  @JoinColumn(name = "trip_type_id", nullable = false)
  private TripType tripType;

  @ManyToOne
  @JoinColumn(name = "vehicle_id", nullable = true)
  private Vehicle vehicle;

  @ManyToOne
  @JoinColumn(name = "driver_id", nullable = true)
  private Driver driver;

  @Column(name = "departure_datetime", nullable = false)
  private LocalDateTime departureDatetime;

  @Column(name = "arrival_datetime", nullable = true)
  private LocalDateTime arrivalDatetime;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @OneToMany(mappedBy = "trip")
  @Fetch(FetchMode.SUBSELECT)
  private Set<Reservation> reservations = new HashSet<>();

  @OneToMany(mappedBy = "trip")
  @Fetch(FetchMode.SUBSELECT)
  private Set<Ticket> tickets = new HashSet<>();

  @OneToMany(mappedBy = "trip")
  @Fetch(FetchMode.SUBSELECT)
  private Set<Diffusion> diffusions = new HashSet<>();

  public TripStatus getStatusEnum() {
    return TripStatus.valueOf(this.status);
  }
}
