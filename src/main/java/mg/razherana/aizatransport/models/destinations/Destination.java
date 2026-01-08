package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "destinations", schema = "public")
@Data
public class Destination extends BasicEntity {

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

}
