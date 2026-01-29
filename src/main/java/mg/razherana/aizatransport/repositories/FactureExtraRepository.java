package mg.razherana.aizatransport.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.FactureExtra;

@Repository
public interface FactureExtraRepository extends JpaRepository<FactureExtra, Integer> {

    @EntityGraph(attributePaths={"factureExtraFilles", "client"})
    @Query("SELECT f FROM FactureExtra f ")
    List<FactureExtra> findAllWithFactureExtraFilles();

    @EntityGraph(attributePaths={"factureExtraFilles", "factureExtraFilles.produitsExtra", "client"})
    @Query("SELECT f FROM FactureExtra f WHERE f.id = :id")
    Optional<FactureExtra> findByIdWithDetails(Integer id);
    
}
