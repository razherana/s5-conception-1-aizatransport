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
import mg.razherana.aizatransport.models.transports.Vehicle;

import java.time.LocalDate;

@Entity
@Table(name = "expenses", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Expense extends BasicExpenseEntity {

  @ManyToOne
  @JoinColumn(name = "vehicle_id")
  private Vehicle vehicle;

  @ManyToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;

  @ManyToOne
  @JoinColumn(name = "type_id", nullable = false)
  private ExpenseType type;

  @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

}
