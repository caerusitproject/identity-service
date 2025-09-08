package com.caerus.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.caerus.identity.client")
public class IdentityServiceTicketingApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityServiceTicketingApplication.class, args);
	}

}
