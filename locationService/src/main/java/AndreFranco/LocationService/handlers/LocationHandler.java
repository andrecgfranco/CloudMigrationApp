package AndreFranco.LocationService.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import AndreFranco.LocationService.dto.LocationMetricsDto;
import AndreFranco.LocationService.dto.LocationSummaryDto;
import AndreFranco.LocationService.dto.TopEntryDto;
import AndreFranco.LocationService.entities.Location;
import AndreFranco.LocationService.entities.Statistics;
import AndreFranco.LocationService.repositories.LocationRepository;
import AndreFranco.LocationService.repositories.StatisticRepository;

@Service
@Transactional(readOnly = true)
public class LocationHandler {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StatisticRepository statisticRepository;

    // -------------------------------------------------------------------------
    // UC1 – Listar todas as localidades
    // -------------------------------------------------------------------------

    // cache hit evita query full-table ao PostgreSQL
    @Cacheable(value = "locations", key = "'all'")
    public List<LocationSummaryDto> listLocations() {
        return locationRepository.findAll()
                .stream()
                .map(loc -> LocationSummaryDto.toDto(loc))
                .toList();
    }

    // cache por id; null não é guardado (unless): evita cachear 404s
    @Cacheable(value = "location-summary", key = "#id", unless = "#result == null")
    public LocationSummaryDto getLocationSummary(Long id) {
        return locationRepository.findById(id)
                .map(LocationSummaryDto::toDto)
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // UC2 – Detalhe por ID
    // -------------------------------------------------------------------------

    // chave composta id+year para diferenciar pedidos com e sem filtro de ano
    @Cacheable(value = "location-by-id", key = "#id + ':' + #year", unless = "#result == null")
    public LocationMetricsDto getLocationById(Long id, Integer year) {
        // retorna null em vez de Optional.empty(): compatível com @Cacheable
        return locationRepository.findById(id).map(location -> {
            List<Statistics> statistics = (year != null)
                    ? statisticRepository.findByLocation_IdAndYear(id, year)
                    : statisticRepository.findByLocation_Id(id);
            return LocationMetricsDto.toDto(location, statistics);
        }).orElse(null);
    }

    // UC2 – Detalhe por código ISO

    @Cacheable(value = "location-by-code", key = "#code + ':' + #year", unless = "#result == null")
    public LocationMetricsDto getLocationByCode(String code, Integer year) {
        // retorna null em vez de Optional.empty(): compatível com @Cacheable
        return locationRepository.findByCode(code).map(location -> {
            List<Statistics> statistics = (year != null)
                    ? statisticRepository.findByLocation_CodeAndYear(code, year)
                    : statisticRepository.findByLocation_Code(code);
            return LocationMetricsDto.toDto(location, statistics);
        }).orElse(null);
    }

    // -------------------------------------------------------------------------
    // UC3 – Comparação entre localidades
    // -------------------------------------------------------------------------

    // chave composta ids+metrics; null quando algum id não existe (não cacheado)
    @Cacheable(value = "locations-compare", key = "#ids + ':' + #metrics", unless = "#result == null")
    public List<LocationMetricsDto> compareLocations(List<Long> ids, List<String> metrics) {
        List<Location> found = locationRepository.findAllById(ids);
        // retorna null em vez de Optional.empty(): compatível com @Cacheable
        if (found.size() != ids.size()) return null;

        List<LocationMetricsDto> result = new ArrayList<>();
        for (Location location : found) {
            List<Statistics> statistics = (metrics != null && !metrics.isEmpty())
                    ? statisticRepository.findLatestByLocationIdAndMetricNameIn(location.getId(), metrics)
                    : statisticRepository.findLatestByLocationId(location.getId());
            result.add(LocationMetricsDto.toDto(location, statistics));
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // UC4 – Top por métrica
    // -------------------------------------------------------------------------

    // endpoint mais pesado computacionalmente: maior benefício de cache
    @Cacheable(value = "locations-top", key = "#metric + ':' + #order + ':' + #limit")
    public List<TopEntryDto> getTopLocations(String metric, String order, int limit) {
        List<Statistics> statistics = "desc".equalsIgnoreCase(order)
                ? statisticRepository.findLatestByMetricNameOrderByValueDesc(metric)
                : statisticRepository.findLatestByMetricNameOrderByValueAsc(metric);

        List<TopEntryDto> result = new ArrayList<>();
        int rank = 1;
        for (Statistics s : statistics) {
            if (rank > limit) break;
            result.add(new TopEntryDto(rank++, s.getLocation().getName(), s.getValue()));
        }
        return result;
    }
}
