package mg.razherana.aizatransport.models.bases;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class BasicExpenseEntity extends BasicEntity {
  @Column(name = "amount", nullable = false, precision = 10)
  private Double amount;
}
