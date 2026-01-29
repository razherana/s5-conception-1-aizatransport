package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "facture_extra", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class FactureExtra extends BasicEntity {

  @ManyToOne
  @JoinColumn(name = "client", nullable = false)
  private Client client;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @OneToMany(mappedBy = "factureExtra", cascade = CascadeType.ALL)
  private Set<FactureExtraFille> factureExtraFilles;

  public Double getTotal() {
    if (factureExtraFilles == null) {
      return 0.0;
    }
    return factureExtraFilles.stream()
        .mapToDouble(fille -> fille.getPrixUnitaire() * fille.getQuantite())
        .sum();
  }
}
