package com.bu.anime_web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // We now rely on GlobalExceptionHandler to forward ALL 404 GET requests to index.html universally.
        // No explicit routing is required here!
    }
}