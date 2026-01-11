package mg.razherana.aizatransport.models.destinations;

import java.time.LocalDateTime;

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
  private LocalDateTime expenseDate;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "status", nullable=false, length=20)
  private String status;

  @Column(name = "amount", nullable=false, precision=10, scale=2)
  private Double amount;

  public ExpenseStatus getStatusEnum(){
    return ExpenseStatus.valueOf(this.status);
  }

  public String getFormattedExpenseDate(){
    if(this.expenseDate == null) return "";
    return this.expenseDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
  }

  public enum ExpenseStatus {
    EN_ATTENTE,
    VALIDEE,
    REJETEE;
  }

}
