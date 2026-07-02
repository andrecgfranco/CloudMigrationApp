package AndreFranco.LocationService.router;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import AndreFranco.LocationService.controllers.LocationController;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationRouter {

    @Autowired
    private LocationController controller;

    // -------------------------------------------------------------------------
    // UC1 – Listar todas as localidades
    // GET /api/v1/locations
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<?> listLocations() {
        return controller.listLocations();
    }

    // -------------------------------------------------------------------------
    // UC2 – Detalhe por código ISO  ← antes de /{id}
    // GET /api/v1/locations/code/PT
    // -------------------------------------------------------------------------

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getLocationByCode(@PathVariable String code, @RequestParam(required = false) Integer year) {
        return controller.getLocationByCode(code, year);
    }

    // -------------------------------------------------------------------------
    // UC3 – Comparação entre localidades  ← antes de /{id}
    // GET /api/v1/locations/compare?ids=1,2&metrics=pli,crime
    // -------------------------------------------------------------------------

    @GetMapping("/compare")
    public ResponseEntity<?> compareLocations(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<String> metrics) {
        return controller.compareLocations(ids, metrics);
    }

    // -------------------------------------------------------------------------
    // UC4 – Top por métrica  ← antes de /{id}
    // GET /api/v1/locations/top?metric=pli&order=asc&limit=5
    // -------------------------------------------------------------------------

    @GetMapping("/top")
    public ResponseEntity<?> getTopLocations(
            @RequestParam(required = false) String metric,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Integer limit) {
        return controller.getTopLocations(metric, order, limit);
    }

    // -------------------------------------------------------------------------
    // UC2 – Detalhe por ID  ← último, senão "compare" é interpretado como ID
    // GET /api/v1/locations/1
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id, @RequestParam(required = false) Integer year ) {
        return controller.getLocationById(id, year);
    }
}