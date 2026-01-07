package com.superhealthclaim.auth_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.superhealthclaim.auth_server.config.AppProperties;

@Configuration
public class AuthorizationServerConfig {

    private final AppProperties appProperties;

    public AuthorizationServerConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
            .logout(logout -> logout
                .logoutSuccessUrl(appProperties.getFrontend().getUrl())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(
                    new LoginUrlAuthenticationEntryPoint("/login")
                )
            )
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        http.getConfigurer(
            org.springframework.security.oauth2.server.authorization
                .config.annotation.web.configurers
                .OAuth2AuthorizationServerConfigurer.class
        ).oidc(Customizer.withDefaults());

        return http.build();
    }
}
