package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "discount_types", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class DiscountType extends BasicEntity {
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "comparator", nullable = false)
    private String comparator;

    public static enum ComparatorType {
        LT, LTE, EQ, GT, GTE
    }
}
