package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;

@Entity
@Table(name = "diffusion_filles", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"diffusion", "revenue"})
public class DiffusionFille extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "diffusion_id", nullable = false)
  private Diffusion diffusion;

  @ManyToOne
  @JoinColumn(name = "revenue_id", nullable = true)
  private Revenue revenue;

  @Column(name = "payment_date", nullable = false)
  private LocalDateTime paymentDate;

  @Column(name = "payment_method", nullable = false, length = 20)
  private String paymentMethod;

  @Column(name = "reference", nullable = true, length = 100)
  private String reference;

  @Column(name = "notes", nullable = true, length = 500)
  private String notes;

  public PaymentMethod getPaymentMethodEnum() {
    return PaymentMethod.valueOf(this.paymentMethod);
  }

  public void setPaymentMethodEnum(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod.name();
  }

  public enum PaymentMethod {
    ESPECES,
    CARTE_BANCAIRE,
    VIREMENT,
    CHEQUE,
    MOBILE_MONEY;
  }
}
