package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.transports.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
  List<Seat> findByVehicleIdOrderBySeatNumberAsc(Integer vehicleId);
}
