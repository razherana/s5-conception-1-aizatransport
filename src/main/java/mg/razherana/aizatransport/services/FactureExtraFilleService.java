package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.FactureExtraFille;
import mg.razherana.aizatransport.repositories.FactureExtraFilleRepository;

@Service
@RequiredArgsConstructor
public class FactureExtraFilleService {

  private final FactureExtraFilleRepository factureExtraFilleRepository;

  public List<FactureExtraFille> findAll() {
    return factureExtraFilleRepository.findAll();
  }

  public Optional<FactureExtraFille> findById(Integer id) {
    return factureExtraFilleRepository.findById(id);
  }

  public FactureExtraFille save(FactureExtraFille factureExtraFille) {
    return factureExtraFilleRepository.save(factureExtraFille);
  }

  public void deleteById(Integer id) {
    factureExtraFilleRepository.deleteById(id);
  }
}
