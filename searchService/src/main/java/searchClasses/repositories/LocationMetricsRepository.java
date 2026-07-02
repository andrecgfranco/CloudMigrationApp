package searchClasses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import searchClasses.entities.LocationMetrics;
import java.util.List;

@Repository
public interface LocationMetricsRepository extends JpaRepository<LocationMetrics, Long> {
    
    
    
   /**
    * Find locations with metric values within a specified range in the most recent year
    */
    @Query("""
            SELECT lm
            FROM LocationMetrics lm
            JOIN FETCH lm.location
            JOIN FETCH lm.metric
            WHERE lm.metric.name = :metricName
            AND lm.value >= :minValue
            AND lm.value <= :maxValue
            AND lm.year = (SELECT MAX(lm2.year) FROM LocationMetrics lm2 WHERE lm2.metric.id = lm.metric.id)
            ORDER BY lm.location.name ASC
            """)
    List<LocationMetrics> findLocationsWithinRange(
            @Param("metricName") String metricName,
            @Param("minValue") Double minValue,
            @Param("maxValue") Double maxValue
    );
    
    /**
     * Find locations with highest increase in a metric over a period
     */
    @Query("""
            SELECT lm
            FROM LocationMetrics lm
            JOIN FETCH lm.location
            JOIN FETCH lm.metric
            WHERE lm.metric.name = :metricName
            AND lm.year >= :startYear
            AND lm.year <= :endYear
            ORDER BY lm.location.id ASC, lm.year ASC
            """)
    List<LocationMetrics> findTrendLocationsIncreasing(
            @Param("metricName") String metricName,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            @Param("limit") Integer limit
    );
    
    /**
     * Find locations with highest decrease in a metric over a period
     */
    @Query("""
            SELECT lm
            FROM LocationMetrics lm
            JOIN FETCH lm.location
            JOIN FETCH lm.metric
            WHERE lm.metric.name = :metricName
            AND lm.year >= :startYear
            AND lm.year <= :endYear
            ORDER BY lm.location.id ASC, lm.year ASC
            """)
    List<LocationMetrics> findTrendLocationsDecreasing(
            @Param("metricName") String metricName,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            @Param("limit") Integer limit
    );
    
    /**
     * Find all metric values for a location within a year range
     */
    @Query("""
            SELECT lm
            FROM LocationMetrics lm
            JOIN FETCH lm.metric
            WHERE lm.location.id = :locationId
            AND lm.year >= :startYear
            AND lm.year <= :endYear
            AND lm.metric.name IN :metricNames
            ORDER BY lm.year ASC, lm.metric.name ASC
            """)
    List<LocationMetrics> findHistoryByLocationAndYearRange(
            @Param("locationId") long locationId,
            @Param("startYear") Short startYear,
            @Param("endYear") Short endYear,
            @Param("metricNames") List<String> metricNames
    );
    
    /**
     * Find all metrics for a location (without year filter)
     */
    @Query("""
            SELECT lm
            FROM LocationMetrics lm
            JOIN FETCH lm.metric
            WHERE lm.location.id = :locationId
            ORDER BY lm.year ASC, lm.metric.name ASC
            """)
    List<LocationMetrics> findAllByLocationId(@Param("locationId") long locationId);

    /**
     * Find metrics for a location by metric names
     */
    List<LocationMetrics> findByLocationIdAndMetricNameIn(Long locationId, List<String> metricNames);

    /**
     * Find location metrics by metric name and year
     */
    List<LocationMetrics> findByMetricNameAndYear(String metricName, Short year);

    /**
     * Find location metrics by metric name ordered by value ascending
     */
    List<LocationMetrics> findByMetricNameOrderByValueAsc(String metricName);

    /**
     * Find location metrics by metric name ordered by value descending
     */
    List<LocationMetrics> findByMetricNameOrderByValueDesc(String metricName);

    List<LocationMetrics> findByMetric_IdOrderByValueAsc(long metricId);
}