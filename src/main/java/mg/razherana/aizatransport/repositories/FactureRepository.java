package mg.razherana.aizatransport.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.Facture;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer> {
  
  @Query("SELECT f FROM Facture f LEFT JOIN FETCH f.factureDiffusionFilles LEFT JOIN FETCH f.facturePayments WHERE f.id = :id")
  Optional<Facture> findByIdWithDetails(@Param("id") Integer id);
  
  @Query("SELECT DISTINCT f FROM Facture f LEFT JOIN FETCH f.factureDiffusionFilles LEFT JOIN FETCH f.facturePayments")
  List<Facture> findAllWithDetails();
  
  @Query("SELECT DISTINCT f FROM Facture f LEFT JOIN FETCH f.factureDiffusionFilles LEFT JOIN FETCH f.facturePayments WHERE f.client.id = :clientId")
  List<Facture> findAllByClientIdWithDetails(@Param("clientId") Integer clientId);
  
  @Query("SELECT f FROM Facture f WHERE f.client.id = :clientId ORDER BY f.factureDate DESC")
  List<Facture> findAllByClientId(@Param("clientId") Integer clientId);
}
