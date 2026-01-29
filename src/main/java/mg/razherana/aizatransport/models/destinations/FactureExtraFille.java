package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "facture_extra_fille", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class FactureExtraFille extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "facture_extra_id", nullable = false)
  private FactureExtra factureExtra;

  @ManyToOne
  @JoinColumn(name = "produits_extra_id", nullable = false)
  private ProduitsExtra produitsExtra;

  @Column(name = "quantite", nullable = false)
  private Integer quantite;

  @Column(name = "prix_unitaire", nullable = false)
  private Double prixUnitaire;

  public Double getSubTotal() {
    return prixUnitaire * quantite;
  }
}
