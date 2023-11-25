package com.infinity.servicedonation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceDonationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDonationApplication.class, args);
    }

}
