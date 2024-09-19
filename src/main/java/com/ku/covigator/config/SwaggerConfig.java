package com.ku.covigator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Covigator API",
                description = "Covigator API 명세서입니다.",
                version = "1.0.0"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("jwt token");
        Components components = new Components().addSecuritySchemes("jwt token", new SecurityScheme()
                .name(HttpHeaders.AUTHORIZATION)
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);

    }

}
