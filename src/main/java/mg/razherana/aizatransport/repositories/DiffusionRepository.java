package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.Diffusion;

@Repository
public interface DiffusionRepository extends JpaRepository<Diffusion, Integer> {
  
  List<Diffusion> findAllByClientId(Integer clientId);
}
