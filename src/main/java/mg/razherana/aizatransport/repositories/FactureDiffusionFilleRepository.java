package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.FactureDiffusionFille;

@Repository
public interface FactureDiffusionFilleRepository extends JpaRepository<FactureDiffusionFille, Integer> {
  
  @Query("SELECT fdf FROM FactureDiffusionFille fdf WHERE fdf.facture.id = :factureId")
  List<FactureDiffusionFille> findAllByFactureId(@Param("factureId") Integer factureId);
  
  @Query("SELECT fdf FROM FactureDiffusionFille fdf WHERE fdf.diffusion.id = :diffusionId")
  List<FactureDiffusionFille> findAllByDiffusionId(@Param("diffusionId") Integer diffusionId);
}
