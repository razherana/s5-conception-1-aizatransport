package mg.razherana.aizatransport.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.Facture;
import mg.razherana.aizatransport.models.destinations.FacturePayment;
import mg.razherana.aizatransport.models.destinations.Reservation;
import mg.razherana.aizatransport.models.destinations.Reservation.ReservationStatus;
import mg.razherana.aizatransport.models.destinations.Revenue;
import mg.razherana.aizatransport.models.destinations.Trip;
import mg.razherana.aizatransport.repositories.RevenueRepository;

@Service
@RequiredArgsConstructor
public class RevenueService {

  private final RevenueRepository revenueRepository;
  private final ReservationService reservationService;
  private final DiffusionService diffusionService;
  private final FactureDiffusionFilleService factureDiffusionFilleService;
  private final FactureExtraService factureExtraService;

  public List<Revenue> findAll() {
    return revenueRepository.findAll();
  }

  public List<Revenue> findAllFiltered(String sourceType, String paymentMethod, String dateMin, String dateMax,
      String sortBy, String sortOrder) {
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

  @Transactional
  public double getCATotalTrip(Trip trip, LocalDateTime min, LocalDateTime max) {
    return getCADiffusions(trip, min, max) + getCAReservations(trip, min, max);
  }

  @Transactional
  public double getCAReservations(Trip trip, LocalDateTime min, LocalDateTime max) {
    return reservationService.findAll().stream()
        .filter(r -> r.getTrip() != null && r.getTrip().getId().equals(trip.getId()))
        .filter(r -> (r.getReservationDate().isAfter(min) || r.getReservationDate().isEqual(min))
            && (r.getReservationDate().isBefore(max) || r.getReservationDate().isEqual(max)))
        .filter(r -> r.getStatusEnum() != ReservationStatus.ANNULE)
        .mapToDouble(Reservation::getTotalAmount)
        .sum();
  }

  @Transactional
  public double getCADiffusions(Trip trip, LocalDateTime min, LocalDateTime max) {
    return diffusionService.findAll().stream()
        .filter(d -> d.getTrip() != null && d.getTrip().getId().equals(trip.getId()))
        .filter(r -> (r.getTrip().getDepartureDatetime().isAfter(min)
            || r.getTrip().getDepartureDatetime().isEqual(min))
            && (r.getTrip().getDepartureDatetime().isBefore(max) || r.getTrip().getDepartureDatetime().isEqual(max)))
        .mapToDouble(d -> d.getAmount())
        .sum();
  }

  @Transactional
  public double getPaidDiffusions(Trip trip, LocalDateTime min, LocalDateTime max) {
    var factures = factureDiffusionFilleService.findAll().stream()
        .filter(ff -> ff.getDiffusion().getTrip().getId() == trip.getId())
        .map(f -> f.getFacture())
        .collect(Collectors.toSet());

    double sum = 0.0;
    for (Facture facture : factures) {
      Set<FacturePayment> paidFactures = facture.getFacturePayments();
      paidFactures.removeIf(f -> !((f.getPaymentDate().isAfter(min) || f.getPaymentDate().equals(min))
              && (f.getPaymentDate().isBefore(max) || f.getPaymentDate().isEqual(max))));
      sum += paidFactures.stream().mapToDouble(fp -> fp.getAmount()).sum();
    }
    return sum;
  }

  @Transactional
  public double getRemainingAmountDiffusions(Trip trip, LocalDateTime min, LocalDateTime max) {
    return getCADiffusions(trip, min, max) - getPaidDiffusions(trip, min, max);
  }

  @Transactional
  public int getNbDiffusions(Trip trip, LocalDateTime min, LocalDateTime max) {
    return trip.getDiffusions().stream()
      .filter(r -> (r.getTrip().getDepartureDatetime().isAfter(min)
            || r.getTrip().getDepartureDatetime().isEqual(min))
            && (r.getTrip().getDepartureDatetime().isBefore(max) || r.getTrip().getDepartureDatetime().isEqual(max)))
      .toList().size();
  }

  @Transactional
  public double getCAExtra(LocalDateTime min, LocalDateTime max) {
    return factureExtraService.findAll().stream()
        .filter(fe -> fe.getDate() != null)
        .filter(fe -> {
          LocalDateTime feDateTime = fe.getDate().atStartOfDay();
          return (feDateTime.isAfter(min) || feDateTime.isEqual(min))
              && (feDateTime.isBefore(max) || feDateTime.isEqual(max));
        })
        .mapToDouble(fe -> fe.getTotal())
        .sum();
  }
}
