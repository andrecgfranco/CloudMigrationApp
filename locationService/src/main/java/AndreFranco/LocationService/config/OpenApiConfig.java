package AndreFranco.LocationService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.server.url:}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Emigração API - Locations Service")
                        .version("1.0")
                        .description("API documentation for the Location Service"));
        
        // Only add server URL if configured (for ingress scenarios)
        if (serverUrl != null && !serverUrl.isBlank()) {
            openAPI.addServersItem(new Server()
                    .url(serverUrl)
                    .description("Location Service"));
        }
        
        return openAPI;
    }
} 
