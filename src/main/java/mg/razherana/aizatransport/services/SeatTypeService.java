package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.transports.SeatType;
import mg.razherana.aizatransport.repositories.SeatTypeRepository;

@Service
@RequiredArgsConstructor
public class SeatTypeService {
    private final SeatTypeRepository seatTypeRepository;

    public List<SeatType> findAll() {
        return seatTypeRepository.findAll();
    }

    public Optional<SeatType> findById(Integer id) {
        return seatTypeRepository.findById(id);
    }

    public SeatType save(SeatType seatType) {
        return seatTypeRepository.save(seatType);
    }

    public void deleteById(Integer id) {
        seatTypeRepository.deleteById(id);
    }

    public List<SeatType> findAllFiltered(String name, String sortBy, String sortOrder) {
        List<SeatType> seatTypes = seatTypeRepository.findAll();

        seatTypes.removeIf(tt -> name != null && !name.isBlank() &&
                !tt.getName().toLowerCase().contains(name.toLowerCase()));

        seatTypes.sort((tt1, tt2) -> {
            int comparison = 0;
            if ("name".equalsIgnoreCase(sortBy)) {
                comparison = tt1.getName().compareToIgnoreCase(tt2.getName());
            }

            if ("id".equalsIgnoreCase(sortBy)) {
                comparison = tt1.getId().compareTo(tt2.getId());
            }

            return "desc".equalsIgnoreCase(sortOrder) ? -comparison : comparison;
        });

        return seatTypes;
    }
}
