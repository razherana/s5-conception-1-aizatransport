package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;

import groovy.transform.EqualsAndHashCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.aizatransport.models.bases.BasicExpenseEntity;
import mg.razherana.aizatransport.models.transports.Seat;

@Entity
@Table(name = "reservations", schema = "public")
@Data
@EqualsAndHashCode(excludes = {"client", "trip", "seat"})
public class Reservation extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Column(name = "reservation_date")
  private LocalDateTime reservationDate;

  @Column(name = "discount", nullable=true)
  private Double discount;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  public ReservationStatus getStatusEnum() {
    return ReservationStatus.valueOf(this.status);
  }

  public enum ReservationStatus {
    RESERVE,
    PAYE,
    ANNULE;
  }

}
