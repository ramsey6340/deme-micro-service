package com.infinity.servicemethodpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceMethodPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMethodPaymentApplication.class, args);
    }

}
