package com.bu.anime_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward Angular routes to index.html so the SPA router can handle them
        registry.addViewController("/search").setViewName("forward:/index.html");
        registry.addViewController("/anime/{id}").setViewName("forward:/index.html");
    }
}
