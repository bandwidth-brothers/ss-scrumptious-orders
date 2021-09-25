package com.ss.scrumptious_orders.swagger;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket swaggerConfiguration() {
        // Return a prepared Docket instance
        return new Docket(DocumentationType.SWAGGER_2).select().paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.any()).build().apiInfo(apiDetails());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo("Customer Microservice API", "Microservice for Scrumptious Food Delivery App", "1.0",
                "Free to use", new Contact("Bandwidth Brothers", "", ""), "API License", "", Collections.emptyList());
    }
}