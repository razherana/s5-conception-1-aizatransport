package mg.razherana.aizatransport.models.transports;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "seat_types", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class SeatType extends BasicEntity {

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "color", nullable = false, unique = true)
  private String color;
}
