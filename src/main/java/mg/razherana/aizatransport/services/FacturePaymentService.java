package mg.razherana.aizatransport.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.FacturePayment;
import mg.razherana.aizatransport.repositories.FacturePaymentRepository;

@Service
@RequiredArgsConstructor
public class FacturePaymentService {

  private final FacturePaymentRepository facturePaymentRepository;

  public List<FacturePayment> findAll() {
    return facturePaymentRepository.findAll();
  }

  public Optional<FacturePayment> findById(Integer id) {
    return facturePaymentRepository.findById(id);
  }

  public FacturePayment save(FacturePayment facturePayment) {
    return facturePaymentRepository.save(facturePayment);
  }

  public void deleteById(Integer id) {
    facturePaymentRepository.deleteById(id);
  }

  public List<FacturePayment> findAllByFactureId(Integer factureId) {
    return facturePaymentRepository.findAllByFactureId(factureId);
  }
}
