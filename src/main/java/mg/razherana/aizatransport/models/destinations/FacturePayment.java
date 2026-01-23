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
@Table(name = "facture_payment", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"facture"})
public class FacturePayment extends BasicExpenseEntity {
    
    @ManyToOne
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;
}
