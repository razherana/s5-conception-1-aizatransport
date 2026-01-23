package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.FacturePayment;

@Repository
public interface FacturePaymentRepository extends JpaRepository<FacturePayment, Integer> {
  
  @Query("SELECT fp FROM FacturePayment fp WHERE fp.facture.id = :factureId ORDER BY fp.paymentDate DESC")
  List<FacturePayment> findAllByFactureId(@Param("factureId") Integer factureId);
}
