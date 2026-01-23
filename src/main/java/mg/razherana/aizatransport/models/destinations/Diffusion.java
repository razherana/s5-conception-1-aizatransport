package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;

@Entity
@Table(name = "diffusions", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"client", "trip"})
public class Diffusion extends BasicExpenseEntity {

  @ManyToOne    
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(name = "designation", nullable = true, length = 255)
  private String designation;
}
