package mg.razherana.aizatransport.models.transports;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "drivers", schema = "public")
@Data
public class Driver extends BasicEntity {

  @Column(name = "full_name", nullable = false, length = 100)
  private String fullName;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "license_number", nullable = false, unique = true, length = 50)
  private String licenseNumber;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

}
