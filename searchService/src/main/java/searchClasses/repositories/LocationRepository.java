package searchClasses.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import searchClasses.entities.Location;
import searchClasses.entities.LocationMetrics;
import java.util.List;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
