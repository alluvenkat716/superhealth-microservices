package com.superhealthclaim.auth_server;

import java.util.UUID;

import com.superhealthclaim.auth_server.config.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

@Configuration
public class RegisteredClientConfig {

    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;

    public RegisteredClientConfig(
            AppProperties appProperties,
            PasswordEncoder passwordEncoder
    ) {
        this.appProperties = appProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient reactClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(appProperties.getOauth().getClientId())
                        .clientSecret(
                                passwordEncoder.encode(
                                        appProperties.getOauth().getClientSecret()
                                )
                        )
                        .clientAuthenticationMethod(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                        )
                        .authorizationGrantType(
                                AuthorizationGrantType.AUTHORIZATION_CODE
                        )
                        .authorizationGrantType(
                                AuthorizationGrantType.REFRESH_TOKEN
                        )
                        .redirectUri(
                                appProperties.getOauth().getRedirectUri()
                        )
                        .scope("openid")
                        .scope("claim.read")
                        .scope("claim.write")
                        .clientSettings(
                                ClientSettings.builder()
                                        .requireAuthorizationConsent(false)
                                        .build()
                        )
                        .build();

        return new InMemoryRegisteredClientRepository(reactClient);
    }
}
