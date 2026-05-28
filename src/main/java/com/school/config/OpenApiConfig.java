package com.school.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // --- The Fix: Explicitly tell SpringDoc the server is at the root ---
                .servers(List.of(new Server().url("/")))
                .info(new Info()
                        .title("School Management System API")
                        .description("""
                                REST API for the School Management System.
                                
                                **Authentication:** Use `POST /auth/login` to obtain a JWT token, 
                                then click **Authorize** and enter: `Bearer <your_token>`
                                
                                **Default admin credentials:**
                                - Email: `admin@school.com`
                                - Password: `Admin@12345`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("School Management Team")
                                .email("support@school.com"))
                        .license(new License()
                                .name("MIT License")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT token here (without the 'Bearer ' prefix)")));
    }
}
