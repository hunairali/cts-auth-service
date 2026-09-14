package dev.umairalishah.ctsauth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adds a "bearerAuth" scheme to the generated OpenAPI document so Swagger UI's "Authorize"
 * button can attach a JWT to requests against the protected endpoints.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI ctsAuthOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CTS Auth Service API")
                        .description("Authentication and user management for the CTS platform")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
