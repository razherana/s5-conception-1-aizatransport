package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.FactureDiffusionFille;
import mg.razherana.aizatransport.repositories.FactureDiffusionFilleRepository;

@Service
@RequiredArgsConstructor
public class FactureDiffusionFilleService {

  private final FactureDiffusionFilleRepository factureDiffusionFilleRepository;

  public List<FactureDiffusionFille> findAll() {
    return factureDiffusionFilleRepository.findAll();
  }

  public Optional<FactureDiffusionFille> findById(Integer id) {
    return factureDiffusionFilleRepository.findById(id);
  }

  public FactureDiffusionFille save(FactureDiffusionFille factureDiffusionFille) {
    return factureDiffusionFilleRepository.save(factureDiffusionFille);
  }

  public void deleteById(Integer id) {
    factureDiffusionFilleRepository.deleteById(id);
  }

  public List<FactureDiffusionFille> findAllByFactureId(Integer factureId) {
    return factureDiffusionFilleRepository.findAllByFactureId(factureId);
  }

  public List<FactureDiffusionFille> findAllByDiffusionId(Integer diffusionId) {
    return factureDiffusionFilleRepository.findAllByDiffusionId(diffusionId);
  }
}
