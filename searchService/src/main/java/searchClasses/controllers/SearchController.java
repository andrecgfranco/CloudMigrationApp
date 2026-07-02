package searchClasses.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import searchClasses.dto.IndicatorDTO;
import searchClasses.dto.LocationSummaryDTO;
import searchClasses.dto.TrendResultDTO;
import searchClasses.handlers.SearchHandler;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@Api(value = "Search API", tags = "Search")
public class SearchController {
    
    @Autowired
    private SearchHandler searchHandler;
    
    @GetMapping
    @Operation(summary = "Filtered search of locations by metric and value range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of locations matching criteria",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid parameters or dataset not available")
    })
    public ResponseEntity<List<LocationSummaryDTO>> filteredSearch(
            @RequestParam String dataset,
            @RequestParam(required = false) Double min_value,
            @RequestParam(required = false) Double max_value) {
        
        List<LocationSummaryDTO> results = searchHandler.filteredSearchLocations(dataset, min_value, max_value);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{location_id}/history")
    @Operation(summary = "Get historical evolution of metrics for a location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historical metrics for the location",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "Location not found")
    })
    public ResponseEntity<List<IndicatorDTO>> getHistory(
            @PathVariable Integer location_id,
            @RequestParam List<String> dataset,
            @RequestParam(required = false) Integer start_year,
            @RequestParam(required = false) Integer end_year) {
        
        List<IndicatorDTO> result = searchHandler.compareLocationsHistory(location_id, dataset, start_year, end_year);
        return ResponseEntity.ok(result);
    }

        @GetMapping("/trends")
    @Operation(summary = "Get location trends for a metric over a period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of locations with metric trends",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<List<TrendResultDTO>> getTrends(
            @RequestParam String dataset,
            @RequestParam Integer years,
            @RequestParam Boolean is_increase,
            @RequestParam(required = false, defaultValue = "10") Integer top) {
        
        List<TrendResultDTO> results = searchHandler.getLocationTrends(dataset, years, is_increase, top);
        return ResponseEntity.ok(results);
    }
}