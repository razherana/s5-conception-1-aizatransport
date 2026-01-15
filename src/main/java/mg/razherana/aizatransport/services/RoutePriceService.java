package mg.razherana.aizatransport.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mg.razherana.aizatransport.models.destinations.RoutePrice;
import mg.razherana.aizatransport.repositories.RoutePriceRepository;

@Service
@RequiredArgsConstructor
public class RoutePriceService {
  
  private final RoutePriceRepository routePriceRepository;

  public List<RoutePrice> findAll() {
    return routePriceRepository.findAll();
  }
}
