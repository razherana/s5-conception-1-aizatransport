package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;

@Entity
@Table(name = "diffusions", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"client", "trip", "diffusionFilles"})
public class Diffusion extends BasicExpenseEntity {

  @ManyToOne    
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @Column(name = "designation", nullable = true, length = 255)
  private String designation;

  @Column(name = "payment_date", nullable = true)
  private LocalDateTime paymentDate;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @OneToMany(mappedBy = "diffusion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DiffusionFille> diffusionFilles = new ArrayList<>();

  public DiffusionStatus getStatusEnum() {
    return DiffusionStatus.valueOf(this.status);
  }

  public void setStatusEnum(DiffusionStatus status) {
    this.status = status.name();
  }

  public enum DiffusionStatus {
    EN_ATTENTE,
    PAYE,
    ANNULE;
  }
}
