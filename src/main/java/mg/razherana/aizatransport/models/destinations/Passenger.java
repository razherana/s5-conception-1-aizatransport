package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDate;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Entity
@Table(name = "passengers", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Passenger extends BasicEntity {

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name ="birth_date", nullable = true)
  private LocalDate birthDate;

  public Integer getAge(LocalDate date){
    return Period.between(birthDate, date).getYears();
  }
}
