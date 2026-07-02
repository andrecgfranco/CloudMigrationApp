package AndreFranco.LocationService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import AndreFranco.LocationService.entities.Location;

public class LocationSummaryDto {

    private Long id;
    private String name;

    @JsonProperty("country_code")
    private String countryCode;

    public LocationSummaryDto() {}

    public LocationSummaryDto(Long id, String name, String countryCode) {
        this.id          = id;
        this.name        = name;
        this.countryCode = countryCode;
    }

    public static LocationSummaryDto toDto(Location loc){
        return new LocationSummaryDto(loc.getId(), loc.getName(), loc.getCode()); 
    }

    public Long getId()                      { return id; }
    public String getName()                  { return name; }
    public String getCountryCode()           { return countryCode; }

    public void setId(Long id)               { this.id = id; }
    public void setName(String name)         { this.name = name; }
    public void setCountryCode(String code)  { this.countryCode = code; }
}