package com.workable.movieramatestapp.configuration;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/badRequest"));
            container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/forbidden"));
            container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/unauthorized"));
            container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/serverError"));
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notFound"));
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/notFound").setViewName("forward:/error");
        registry.addViewController("/serverError").setViewName("forward:/error");
        registry.addViewController("/unauthorized").setViewName("forward:/error");
        registry.addViewController("/forbidden").setViewName("forward:/error");
        registry.addViewController("/badRequest").setViewName("forward:/error");
    }


}
