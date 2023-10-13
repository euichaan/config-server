package com.jydev.configserver.config;

import com.jydev.configserver.actuator.ActuatorLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient actuatorWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .filter(actuatorLogger().logRequest())
                .filter(actuatorLogger().logResponse())
                .build();
    }

    @Bean
    public ActuatorLogger actuatorLogger() {
        return new ActuatorLogger();
    }
}
