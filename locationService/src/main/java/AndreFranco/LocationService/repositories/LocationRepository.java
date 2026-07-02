package AndreFranco.LocationService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import AndreFranco.LocationService.entities.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>{
    
    Optional<Location> findByCode(String code);

}
