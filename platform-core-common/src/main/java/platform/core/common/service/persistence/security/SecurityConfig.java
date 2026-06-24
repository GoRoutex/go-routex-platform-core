package platform.core.common.service.persistence.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import vn.com.go.routex.identity.security.exception.CustomAccessDeniedHandler;
import vn.com.go.routex.identity.security.exception.CustomAuthenticationEntryPoint;
import vn.com.go.routex.identity.security.jwt.JwtAuthenticationFilter;

import java.util.List;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ApiFilter apiFilter;

    public SecurityConfig(ApiFilter apiFilter) {
        this.apiFilter = apiFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurerCustomizer())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/api/v1/booking-service/trips/hold-seat",
                                "/api/v1/booking-service/trips/hold-seat/round-trip",
                                "/api/v1/booking-service/payments/context",
                                "/api/v1/internal/**",
                                "/api/v1/management/trip-service/search",
                                "/api/v1/management/merchant-service/**",
                                "/api/v1/management/application-form/**",
                                "/api/v1/management/route-service/**",
                                "/api/v1/management/seat-diagram/search",
                                "/api/v1/management/seat-diagram/search/round-trip",
                                "/api/v1/merchant-service/internal/**",
                                "/api/v1/merchant-service/provinces/search",
                                "/api/v1/merchant-services/campaigns/validate",
                                "/api/v1/payment-service/get-payment-url/batch",
                                "/api/v1/management/authorities/**",
                                "/api/v1/payment-service/vnpay-ipn",
                                "/api/v1/management/route-service/**",
                                "/api/v1/payment-service/**",
                                "/error",
                                "/api/v1/media/**",
                                "/api/v1/route-service/search",
                                "/api/v1/route-service/fetch",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(apiFilter, JwtAuthenticationFilter.class)
                .build();
    }

    private Customizer<CorsConfigurer<HttpSecurity>> corsConfigurerCustomizer() {
        return cors -> cors.configurationSource(corsConfigurationSource());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            var corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*", "https://routex-go.com", "https://www.routex-go.com"));
            corsConfig.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()));
            corsConfig.setAllowedHeaders(List.of("*"));
            corsConfig.setExposedHeaders(List.of("Authorization", "Content-Disposition", "Location", "X-Request-Id", "RT-REQUEST-ID"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setMaxAge(3600L);
            return corsConfig;
        };
    }
}
