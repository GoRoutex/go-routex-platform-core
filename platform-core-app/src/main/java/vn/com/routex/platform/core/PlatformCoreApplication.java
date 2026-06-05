package vn.com.routex.platform.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EntityScan(basePackages = {
        "platform.core.common.service",
        "platform.merchant",
        "platform.booking.service",
        "platform.driver.service",
        "platform.management.service",
        "platform.payment.service"
})
@EnableJpaRepositories(
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        basePackages = {
                "platform.core.common.service",
                "platform.merchant",
                "platform.booking.service",
                "platform.driver.service",
                "platform.management.service",
                "platform.payment.service"
        }
)
@ConfigurationPropertiesScan(basePackages = {
        "vn.com.routex.platform",
        "platform.core.common.service",
        "platform.merchant",
        "platform.booking.service",
        "platform.driver.service",
        "platform.management.service",
        "platform.payment.service"
})
@SpringBootApplication
@ComponentScan(
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        basePackages = {
                "vn.com.routex.platform",
                "platform.core.common.service",
                "platform.merchant",
                "platform.booking.service",
                "platform.driver.service",
                "platform.management.service",
                "platform.payment.service"
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.(MerchantPlatformApplication|BookingServiceApplication|RoutexDriverServiceApplication|ManagementServiceApplication|PaymentApplication)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.persistence\\.security\\.(SecurityConfig|ApiFilter)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.persistence\\.security\\.(SecurityConfig|ApiFilter)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.kafka\\.config\\.(KafkaConfig|KafkaErrorHandlerConfig)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.cache\\.config\\.RedisConfig"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.redis\\.config\\.RedisConfig"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.cache\\.redisson\\.config\\.RedissonConfig"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.redisson\\.config\\.RedissonConfig"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.persistence\\.config\\.(ApplicationConfig|OpenApiConfig|JpaAuditingConfig)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.persistence\\.config\\.(ApplicationConfig|OpenApiConfig|JpaAuditingConfig)"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*\\.infrastructure\\.persistence\\.exception[s]?\\.ExceptionHandlerAdvice")
        }
)
public class PlatformCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformCoreApplication.class, args);
    }
}
