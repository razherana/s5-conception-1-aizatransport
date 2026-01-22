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
@Table(name = "revenues", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"reservation", "diffusion"})
public class Revenue extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "reservation_id", nullable = true)
  private Reservation reservation;

  @ManyToOne
  @JoinColumn(name = "diffusion_id", nullable = true)
  private Diffusion diffusion;

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

  public enum PaymentMethod {
    ESPECES,
    CARTE_BANCAIRE,
    VIREMENT,
    CHEQUE,
    MOBILE_MONEY;
  }

  public String getSourceType() {
    if (reservation != null) {
      return "Réservation";
    } else if (diffusion != null) {
      return "Diffusion";
    }
    return "Inconnu";
  }

  public Integer getSourceId() {
    if (reservation != null) {
      return reservation.getId();
    } else if (diffusion != null) {
      return diffusion.getId();
    }
    return null;
  }
}
