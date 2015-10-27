package com.tchepannou.kiosk.rss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.tchepannou.kiosk.rss.service.GreetingService;
import com.tchepannou.kiosk.rss.service.impl.GreetingServiceImpl;

/**
 * Declare your services here!
 */
@Configuration
public class AppConfig {
    @Bean
    GreetingService greetingService (){
        return new GreetingServiceImpl();
    }
}
