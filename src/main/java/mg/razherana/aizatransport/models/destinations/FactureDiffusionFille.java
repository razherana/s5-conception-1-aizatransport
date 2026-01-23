package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "facture_diffusion_fille", schema = "public")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"facture", "diffusion"})
public class FactureDiffusionFille extends BasicEntity {
    
    @ManyToOne
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;
    
    @ManyToOne
    @JoinColumn(name = "diffusion_id", nullable = false)
    private Diffusion diffusion;
}
