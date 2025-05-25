package com.example.plagiarismapp.config;

import org.example.service.CoreServiceReactive;
import org.example.token.strategy.TokenCollectorManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public TokenCollectorManager tokenCollectorManager() {
        return new TokenCollectorManager();
    }

    @Bean
    public CoreServiceReactive coreServiceReactive(TokenCollectorManager tokenCollectorManager) {
        return new CoreServiceReactive(tokenCollectorManager);
    }
}
