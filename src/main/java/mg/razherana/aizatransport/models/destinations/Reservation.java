package mg.razherana.aizatransport.models.destinations;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;
import mg.razherana.aizatransport.models.transports.Seat;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations", schema = "public")
@Data
public class Reservation extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "passenger_id", nullable = false)
  private Passenger passenger;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Column(name = "reservation_date")
  private LocalDateTime reservationDate;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

}
