package com.coach.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AnthropicConfig {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.version}")
    private String apiVersion;

    @Bean
    public WebClient anthropicWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", apiVersion)
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024) // 2MB
                )
                .build();
    }
}
