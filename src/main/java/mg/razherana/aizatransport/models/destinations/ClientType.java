package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "client_types", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ClientType extends BasicEntity {

  @Column(name = "name", nullable = false, unique = true, length = 50)
  private String name;

  @Column(name = "active", nullable = false)
  private Boolean active = true;

}
