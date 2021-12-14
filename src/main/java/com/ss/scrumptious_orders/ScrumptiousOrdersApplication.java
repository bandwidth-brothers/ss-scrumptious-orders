package com.ss.scrumptious_orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = { "com.ss.scrumptious.common_entities" })
public class ScrumptiousOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrumptiousOrdersApplication.class, args);
	}

}
