package mg.razherana.aizatransport.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Revenue;
import mg.razherana.aizatransport.repositories.RevenueRepository;

@Service
@RequiredArgsConstructor
public class RevenueService {

  private final RevenueRepository revenueRepository;

  public List<Revenue> findAll() {
    return revenueRepository.findAll();
  }

  public List<Revenue> findAllFiltered(String sourceType, String paymentMethod, String dateMin, String dateMax, String sortBy, String sortOrder) {
    List<Revenue> revenues = revenueRepository.findAll();

    // Filtrage par type de source
    if (sourceType != null && !sourceType.isEmpty()) {
      revenues = revenues.stream()
          .filter(r -> r.getSourceType().equalsIgnoreCase(sourceType))
          .collect(Collectors.toList());
    }

    // Filtrage par méthode de paiement
    if (paymentMethod != null && !paymentMethod.isEmpty()) {
      revenues = revenues.stream()
          .filter(r -> r.getPaymentMethod().equalsIgnoreCase(paymentMethod))
          .collect(Collectors.toList());
    }

    // Filtrage par date min
    if (dateMin != null && !dateMin.isEmpty()) {
      LocalDateTime minDateTime = LocalDate.parse(dateMin).atStartOfDay();
      revenues = revenues.stream()
          .filter(r -> r.getPaymentDate() != null && !r.getPaymentDate().isBefore(minDateTime))
          .collect(Collectors.toList());
    }

    // Filtrage par date max
    if (dateMax != null && !dateMax.isEmpty()) {
      LocalDateTime maxDateTime = LocalDate.parse(dateMax).atTime(LocalTime.MAX);
      revenues = revenues.stream()
          .filter(r -> r.getPaymentDate() != null && !r.getPaymentDate().isAfter(maxDateTime))
          .collect(Collectors.toList());
    }

    // Tri
    if (sortBy != null && !sortBy.isEmpty()) {
      Comparator<Revenue> comparator = getComparator(sortBy);
      if (comparator != null) {
        if ("desc".equalsIgnoreCase(sortOrder)) {
          comparator = comparator.reversed();
        }
        revenues = revenues.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
      }
    }

    return revenues;
  }

  private Comparator<Revenue> getComparator(String sortBy) {
    return switch (sortBy.toLowerCase()) {
      case "paymentdate" -> Comparator.comparing(Revenue::getPaymentDate);
      case "amount" -> Comparator.comparing(Revenue::getAmount);
      case "paymentmethod" -> Comparator.comparing(Revenue::getPaymentMethod);
      case "sourcetype" -> Comparator.comparing(Revenue::getSourceType);
      default -> null;
    };
  }

  public Optional<Revenue> findById(Integer id) {
    return revenueRepository.findById(id);
  }

  public Revenue save(Revenue revenue) {
    if (revenue.getPaymentDate() == null) {
      revenue.setPaymentDate(LocalDateTime.now());
    }
    return revenueRepository.save(revenue);
  }

  public void deleteById(Integer id) {
    revenueRepository.deleteById(id);
  }

  public List<String> getAllPaymentMethods() {
    return Arrays.stream(Revenue.PaymentMethod.values())
        .map(Enum::name)
        .collect(Collectors.toList());
  }
}
