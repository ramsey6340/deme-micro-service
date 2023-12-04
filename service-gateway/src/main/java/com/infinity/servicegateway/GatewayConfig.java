package com.infinity.servicegateway;

import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    //@Bean
    /*public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/api/v1/methodPayments/**").uri("lb://service-method-payment"))
                .route(r->r.path("/api/v1/auth/**").uri("lb://service-auth"))
                .route(r->r.path("/api/v1/users/**").uri("lb://service-user"))
                .route(r->r.path("/api/v1/causes/**").uri("lb://service-cause"))
                .route(r->r.path("/api/v1/admins/**").uri("lb://service-admin"))
                .route(r->r.path("/api/v1/organizations/**").uri("lb://service-organization"))
                .route(r->r.path("/api/v1/activities/**").uri("lb://service-activity"))
                .route(r->r.path("/api/v1/testimonies/**").uri("lb://service-testimony"))
                .route(r->r.path("/api/v1/donations/**").uri("lb://service-donation"))
                .build();
    }*/

    @Bean
    DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
            ReactiveDiscoveryClient reactiveDiscoveryClient, DiscoveryLocatorProperties discoveryLocatorProperties
    ){

        return new DiscoveryClientRouteDefinitionLocator(reactiveDiscoveryClient,discoveryLocatorProperties);
    }
}
