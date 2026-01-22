package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.DiffusionFille;
import mg.razherana.aizatransport.repositories.DiffusionFilleRepository;

@Service
@RequiredArgsConstructor
public class DiffusionFilleService {

  private final DiffusionFilleRepository diffusionFilleRepository;

  public List<DiffusionFille> findAll() {
    return diffusionFilleRepository.findAll();
  }

  public Optional<DiffusionFille> findById(Integer id) {
    return diffusionFilleRepository.findById(id);
  }

  public DiffusionFille save(DiffusionFille diffusionFille) {
    return diffusionFilleRepository.save(diffusionFille);
  }

  public void deleteById(Integer id) {
    diffusionFilleRepository.deleteById(id);
  }
}
