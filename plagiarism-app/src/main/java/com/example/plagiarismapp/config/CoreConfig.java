package com.example.plagiarismapp.config;

import org.example.service.CoreService;
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
    public CoreService coreService(TokenCollectorManager tokenCollectorManager) {
        return new CoreService(tokenCollectorManager);
    }
}
