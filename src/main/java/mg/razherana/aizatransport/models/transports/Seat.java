package mg.razherana.aizatransport.models.transports;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "seats", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Seat extends BasicEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "vehicle_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Vehicle vehicle;

  @Column(name = "seat_number", nullable = false, length = 3)
  private String seatNumber;
  
  @ManyToOne
  @JoinColumn(name = "seat_type_id", nullable = false)
  private SeatType seatType;

  @Column(name = "available", nullable = false)
  private boolean available;

}
