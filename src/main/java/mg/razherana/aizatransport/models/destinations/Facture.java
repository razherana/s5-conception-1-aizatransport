package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "facture_diffusion", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"client", "factureDiffusionFilles", "facturePayments"})
public class Facture extends BasicEntity {
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @Column(name = "facture_date", nullable = false)
    private LocalDateTime factureDate;
    
    @Column(name = "ref", nullable = false, length = 100)
    private String ref;
    
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FactureDiffusionFille> factureDiffusionFilles = new HashSet<>();
    
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FacturePayment> facturePayments = new HashSet<>();
}
