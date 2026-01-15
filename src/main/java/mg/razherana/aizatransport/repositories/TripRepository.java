package mg.razherana.aizatransport.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.razherana.aizatransport.models.destinations.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {
  @Query("SELECT t FROM Trip t")
  @EntityGraph(attributePaths = { "reservations", "tickets" })
  public List<Trip> findAllWithReservationsAndTickets();

  @EntityGraph(attributePaths = {
      "vehicle",
      "vehicle.seats",
      "vehicle.seats.seatType"
  })
  @Query("SELECT t FROM Trip t")
  public List<Trip> findAllWithVehicleSeatsAndSeatType();
}
