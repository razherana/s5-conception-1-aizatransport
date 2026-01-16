package mg.razherana.aizatransport.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.DiscountType;

@Repository
public interface DiscountTypeRepository extends JpaRepository<DiscountType, Integer> {
    
}
