package mg.razherana.aizatransport.models.transports;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "drivers", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Driver extends BasicEntity {

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "license_number", nullable = false, unique = true, length = 50)
  private String licenseNumber;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  public DriverStatus getStatusEnum() {
    return DriverStatus.valueOf(this.status);
  }

  public enum DriverStatus {
    DISPONIBLE,
    EN_SERVICE,
    EN_CONGE,
    INDISPONIBLE;
  }
}
