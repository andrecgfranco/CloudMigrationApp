package AndreFranco.LocationService;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@Sql(scripts = "/test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class LocationApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;



    // ---- UC1: list all locations ----

    @Test
    void listLocations_returns200WithAllLocations() throws Exception {
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // ---- UC2: location by ID ----

    @Test
    void getLocationById_existingId_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Portugal"))
                .andExpect(jsonPath("$.country_code").value("PT"));
    }

    @Test
    void getLocationById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/locations/999"))
                .andExpect(status().isNotFound());
    }

    // ---- UC2: location by ISO code ----

    @Test
    void getLocationByCode_existingCode_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/code/PT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country_code").value("PT"))
                .andExpect(jsonPath("$.metrics").isArray());
    }

    @Test
    void getLocationByCode_unknownCode_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/locations/code/XX"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLocationByCode_invalidLength_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/locations/code/POR"))
                .andExpect(status().isBadRequest());
    }

    // ---- UC3: compare locations ----

    @Test
    void compareLocations_twoValidIds_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/compare").param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void compareLocations_singleId_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/locations/compare").param("ids", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void compareLocations_withMetricFilter_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/compare")
                        .param("ids", "1", "2")
                        .param("metrics", "pli"))
                .andExpect(status().isOk());
    }

    // ---- UC4: top by metric ----

    @Test
    void getTopLocations_validMetric_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/locations/top").param("metric", "pli"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1));
    }

    @Test
    void getTopLocations_withLimit_returnsCorrectCount() throws Exception {
        mockMvc.perform(get("/api/v1/locations/top")
                        .param("metric", "pli")
                        .param("order", "asc")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTopLocations_noMetric_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/locations/top"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTopLocations_invalidOrder_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/locations/top")
                        .param("metric", "pli")
                        .param("order", "sideways"))
                .andExpect(status().isBadRequest());
    }

}