package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;
import mg.razherana.aizatransport.models.transports.Seat;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Ticket extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "passenger_id", nullable = false)
  private Passenger passenger;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Column(name = "purchase_date")
  private LocalDateTime purchaseDate;

}
