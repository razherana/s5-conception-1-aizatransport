package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "expense_types", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseType extends BasicEntity {

  @Column(name = "type_name", nullable = false, unique = true, length = 50)
  private String typeName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

}
