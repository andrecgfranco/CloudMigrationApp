package juliana.auth_forum_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    // This configuration allows lazy channel initialization.
    // The @GrpcClient annotation will create channels lazily on first use,
    // and the channel will automatically reconnect if the server comes online later.
}
