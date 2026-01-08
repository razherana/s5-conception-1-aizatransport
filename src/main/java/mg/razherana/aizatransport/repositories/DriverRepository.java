package mg.razherana.aizatransport.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.transports.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {
}
