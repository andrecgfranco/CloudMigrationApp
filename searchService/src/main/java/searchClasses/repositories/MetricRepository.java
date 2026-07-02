package searchClasses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import searchClasses.entities.Metric;


@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
        Optional<Metric> findByName(String name);
}
