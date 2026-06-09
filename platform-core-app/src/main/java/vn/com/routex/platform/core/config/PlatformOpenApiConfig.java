package vn.com.routex.platform.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class PlatformOpenApiConfig {

    @Bean
    public OpenAPI goRoutexOpenApi() {
        return new OpenAPI()
                .info(new Info().title("GoRoutex Platform Core API").version("v1"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

    @Bean
    public GroupedOpenApi platformCoreApi() {
        return GroupedOpenApi.builder()
                .group("platform-core")
                .packagesToScan(
                        "platform.booking.service.interfaces",
                        "platform.driver.service.interfaces",
                        "platform.management.service.interfaces",
                        "platform.merchant.service.interfaces",
                        "platform.payment.service.interfaces"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi bookingApi() {
        return GroupedOpenApi.builder()
                .group("booking")
                .packagesToScan("platform.booking.service.interfaces")
                .build();
    }

    @Bean
    public GroupedOpenApi driverApi() {
        return GroupedOpenApi.builder()
                .group("driver")
                .packagesToScan("platform.driver.service.interfaces")
                .build();
    }

    @Bean
    public GroupedOpenApi managementApi() {
        return GroupedOpenApi.builder()
                .group("management")
                .packagesToScan("platform.management.service.interfaces")
                .build();
    }

    @Bean
    public GroupedOpenApi merchantApi() {
        return GroupedOpenApi.builder()
                .group("merchant")
                .packagesToScan("platform.merchant.service.interfaces")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("payment")
                .packagesToScan("platform.payment.service.interfaces")
                .build();
    }
}
