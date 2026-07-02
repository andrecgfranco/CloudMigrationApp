package AndreFranco.LocationService.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import AndreFranco.LocationService.entities.Statistics;

@Repository
public interface StatisticRepository extends JpaRepository<Statistics, Long> {

    // -------------------------------------------------------------------------
    // UC2 e UC3 — todas as estatísticas de uma ou mais localidades
    // -------------------------------------------------------------------------

    List<Statistics> findByLocation_Id(Long locationId);
    List<Statistics> findByLocation_IdIn(List<Long> ids);
    List<Statistics> findByLocation_Code(String code);
    List<Statistics> findByLocation_CodeIn(List<String> codes);
    List<Statistics> findByLocation_Name(String name);
    List<Statistics> findByLocation_NameIn(List<String> names);

    List<Statistics> findByLocation_IdAndYear(Long locationId, int year);
    List<Statistics> findByLocation_CodeAndYear(String code, int year);

    // UC3 — filtradas por métricas específicas
    List<Statistics> findByLocation_IdAndMetric_NameIn(Long locationId, List<String> metricNames);
    List<Statistics> findByLocation_IdInAndMetric_NameIn(List<Long> ids, List<String> metricNames);

    // -------------------------------------------------------------------------
    // UC2 e UC3 — ano mais recente por localidade e métrica
    // -------------------------------------------------------------------------

    @Query("SELECT s FROM Statistics s WHERE s.location.id = :locationId AND s.year = (SELECT MAX(s2.year) FROM Statistics s2 WHERE s2.location.id = s.location.id AND s2.metric.id = s.metric.id)")
    List<Statistics> findLatestByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT s FROM Statistics s WHERE s.location.id = :locationId AND s.metric.name IN :metricNames AND s.year = (SELECT MAX(s2.year) FROM Statistics s2 WHERE s2.location.id = s.location.id AND s2.metric.id = s.metric.id)")
    List<Statistics> findLatestByLocationIdAndMetricNameIn(@Param("locationId") Long locationId, @Param("metricNames") List<String> metricNames);

    // -------------------------------------------------------------------------
    // UC4 — ordenadas por valor para o /top
    // -------------------------------------------------------------------------

    List<Statistics> findByMetric_NameOrderByValueAsc(String metricName);
    List<Statistics> findByMetric_NameOrderByValueDesc(String metricName);

    @Query("SELECT s FROM Statistics s WHERE s.metric.name = :metricName AND s.year = (SELECT MAX(s2.year) FROM Statistics s2 WHERE s2.location.id = s.location.id AND s2.metric.id = s.metric.id) ORDER BY s.value ASC")
    List<Statistics> findLatestByMetricNameOrderByValueAsc(@Param("metricName") String metricName);

    @Query("SELECT s FROM Statistics s WHERE s.metric.name = :metricName AND s.year = (SELECT MAX(s2.year) FROM Statistics s2 WHERE s2.location.id = s.location.id AND s2.metric.id = s.metric.id) ORDER BY s.value DESC")
    List<Statistics> findLatestByMetricNameOrderByValueDesc(@Param("metricName") String metricName);
}