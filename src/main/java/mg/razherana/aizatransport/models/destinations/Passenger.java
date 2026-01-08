package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "passengers", schema = "public")
@Data
public class Passenger extends BasicEntity {

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "phone", length = 30)
  private String phone;

}
