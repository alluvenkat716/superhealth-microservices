package com.superhealthclaim.claimservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final AppProperties appProperties;

    public SecurityConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/health",
                        "/actuator/health"
                    ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/claims/**")
                    .hasAuthority("SCOPE_claim.read")

                .requestMatchers(HttpMethod.POST, "/api/claims/**")
                    .hasAuthority("SCOPE_claim.write")

                .requestMatchers(HttpMethod.PUT, "/api/claims/**")
                    .hasAuthority("SCOPE_claim.write")

                .requestMatchers(HttpMethod.DELETE, "/api/claims/**")
                    .hasAuthority("SCOPE_claim.write")

                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(
                List.of(appProperties.getFrontend().getUrl())
        );
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
