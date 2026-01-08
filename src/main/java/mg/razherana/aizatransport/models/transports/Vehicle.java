package mg.razherana.aizatransport.models.transports;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicEntity;

@Data
@Entity
@Table(name = "vehicles", schema = "public")
public class Vehicle extends BasicEntity {
  @Column(unique = true, nullable = false)
  private String plateNumber;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private Integer capacity;

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private LocalDate createdAt;
}
