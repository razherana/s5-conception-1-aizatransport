package mg.razherana.aizatransport.controllers.destinations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteStatisticsDTO {
  private int totalTrips;
  private int completedTrips;
  private int cancelledTrips;
  private int ongoingTrips;
  private BigDecimal totalRevenue;
  private BigDecimal averageRevenuePerTrip;
  
  // Chart data for trips over time
  private List<String> tripChartLabels;
  private List<Integer> tripChartData;
  
  // Chart data for status distribution
  private List<String> statusLabels;
  private List<Integer> statusCounts;
  
  // Top drivers
  private Map<String, Integer> topDrivers;
  private int maxDriverTrips;
  
  // Monthly revenue chart
  private List<String> revenueMonthLabels;
  private List<BigDecimal> revenueMonthData;
}
