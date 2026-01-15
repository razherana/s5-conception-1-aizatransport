package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.transports.Seat;
import mg.razherana.aizatransport.models.transports.SeatType;
import mg.razherana.aizatransport.models.transports.Vehicle;
import mg.razherana.aizatransport.repositories.SeatRepository;


@Service
@RequiredArgsConstructor
public class SeatService {

  private static final int SEATS_PER_ROW = 5;

  private final SeatRepository seatRepository;
  private final VehicleService vehicleService;
  private final SeatTypeService seatTypeService;

  public List<Seat> findByVehicleId(Integer vehicleId) {
    return seatRepository.findByVehicleIdOrderBySeatNumberAsc(vehicleId);
  }

  public Optional<Seat> findById(Integer id) {
    return seatRepository.findById(id);
  }

  public Seat save(Seat seat) {
    return seatRepository.save(seat);
  }

  public void deleteById(Integer id) {
    seatRepository.deleteById(id);
  }

  public void generateSeatsForVehicle(Integer vehicleId) {
    Optional<Vehicle> vehicleOpt = vehicleService.findById(vehicleId);
    if (vehicleOpt.isPresent()) {
      Vehicle vehicle = vehicleOpt.get();
      List<Seat> existingSeats = findByVehicleId(vehicleId);

      if (existingSeats.isEmpty() && vehicle.getCapacity() != null) {
        Optional<SeatType> seatTypeOpt = seatTypeService.findById(1);
        if (seatTypeOpt.isPresent()) {
          SeatType seatType = seatTypeOpt.get(); 
          for (int i = 1; i <= vehicle.getCapacity(); i++) {
            Seat seat = new Seat();
            seat.setVehicle(vehicle);
            seat.setSeatNumber(generateSeatNumber(i));
            seat.setSeatType(seatType);
            seat.setAvailable(true);
            seatRepository.save(seat);
          }
        } else {
          throw new IllegalArgumentException("Type de siège par défaut");
        }
      }
    }
  }

  private String generateSeatNumber(int position) {
    int rowIndex = (position - 1) / SEATS_PER_ROW;
    int seatInRow = ((position - 1) % SEATS_PER_ROW) + 1;
    char rowLetter = (char) ('A' + rowIndex);
    return rowLetter + String.valueOf(seatInRow);
  }
}
