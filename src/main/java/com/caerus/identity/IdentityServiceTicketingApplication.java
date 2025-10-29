package com.caerus.identity;

import com.caerus.identity.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableFeignClients(basePackages = "com.caerus.identity.client")
public class IdentityServiceTicketingApplication {

  public static void main(String[] args) {
    SpringApplication.run(IdentityServiceTicketingApplication.class, args);
  }
}
