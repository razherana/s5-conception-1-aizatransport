package mg.razherana.aizatransport.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.Diffusion;

@Repository
public interface DiffusionRepository extends JpaRepository<Diffusion, Integer> {
  
  @Query("SELECT d FROM Diffusion d LEFT JOIN FETCH d.diffusionFilles WHERE d.id = :id")
  Optional<Diffusion> findByIdWithPayments(@Param("id") Integer id);
  
  @Query("SELECT DISTINCT d FROM Diffusion d LEFT JOIN FETCH d.diffusionFilles")
  List<Diffusion> findAllWithPayments();
  
  @Query("SELECT DISTINCT d FROM Diffusion d LEFT JOIN FETCH d.diffusionFilles WHERE d.client.id = :clientId")
  List<Diffusion> findAllByClientIdWithPayments(@Param("clientId") Integer clientId);
}
