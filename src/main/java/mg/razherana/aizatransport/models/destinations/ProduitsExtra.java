package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "produits_extras", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class ProduitsExtra extends BasicEntity {

  @Column(name = "nom", length = 100, nullable = false)
  private String nom;

  @Column(name = "prix", nullable = false)
  private Double prix;
}
