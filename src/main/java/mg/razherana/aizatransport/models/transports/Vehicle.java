package mg.razherana.aizatransport.models.transports;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vehicles", schema = "public")
public class Vehicle extends BasicEntity {
  @Column(unique = true, nullable = false)
  private String plateNumber;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private Integer capacity;

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private LocalDate createdAt;

  @OneToMany(mappedBy = "vehicle")
  private Set<Seat> seats;

  public VehicleStatus getStatusEnum() {
    return VehicleStatus.valueOf(this.status);
  }

  public enum VehicleStatus {
    DISPONIBLE,
    EN_SERVICE,
    EN_MAINTENANCE,
    HORS_SERVICE;
  }
}
