package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "destinations", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Destination extends BasicEntity {

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

}
