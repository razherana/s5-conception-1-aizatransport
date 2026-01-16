package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.razherana.aizatransport.models.bases.BasicEntity;
import mg.razherana.aizatransport.models.transports.SeatType;

@Entity
@Table(name = "discounts", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Discount extends BasicEntity {
    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "trip_type_id", nullable = false)
    private TripType tripType;

    @ManyToOne
    @JoinColumn(name = "seat_type_id", nullable = false)
    private SeatType seatType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @ManyToOne
    @JoinColumn(name = "discount_type_id", nullable = false)
    private DiscountType discountType;
}
