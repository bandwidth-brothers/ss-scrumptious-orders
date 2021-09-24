package com.ss.scrumptious_orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScrumptiousOrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrumptiousOrdersApplication.class, args);
	}

}
