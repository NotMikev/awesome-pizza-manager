package com.awesome.pizza.order.manager.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.awesome.pizza.order.manager.filter.ApiAuditFilter;

import jakarta.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiAuditFilter apiAuditFilter;

    public WebConfig(ApiAuditFilter apiAuditFilter) {
        this.apiAuditFilter = apiAuditFilter;
    }

    /**
     * Registra il filtro ApiAuditFilter nella catena servlet, con priorità alta
     * (ordine 1) per leggere request/response prima degli altri.
     */
    @Bean
    public FilterRegistrationBean<Filter> apiAuditFilterRegistration() {

        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(apiAuditFilter);

        // Solo per le api
        registration.addUrlPatterns("/api/*"); //context path viene aggiunto automaticamente
        registration.setOrder(1);

        return registration;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        // Redirect a Swagger UI in caso di atterraggio su root
        registry
                .addRedirectViewController("/", "/swagger-ui.html");
        // Aggiunta viste UI Swagger
        registry
                .addViewController("/swagger-ui.html")
                .setViewName("forward:/swagger-ui/index.html");
    }
}
