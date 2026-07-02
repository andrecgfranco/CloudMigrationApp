package userClasses;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@Sql(scripts = "/test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class UserApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;



    @Test
    void getAllUsers_returns200WithAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getUsersByName_validName_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("name", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getUsersByName_unknownName_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users").param("name", "UnknownUser"))
                .andExpect(status().isNotFound());
    }



    @Test
    void getUserByUsername_validUsername_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/users/byUserName/john_doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserByUsername_unknownUsername_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/byUserName/nonexistent"))
                .andExpect(status().isNotFound());
    }



    @Test
    void getUserById_validId_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/users/byId/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void getUserById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/byId/999"))
                .andExpect(status().isNotFound());
    }
}
