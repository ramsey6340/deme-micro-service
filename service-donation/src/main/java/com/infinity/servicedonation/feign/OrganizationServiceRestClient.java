package com.infinity.servicedonation.feign;

import com.infinity.servicedonation.models.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-organization")
public interface OrganizationServiceRestClient {

    @GetMapping("api/v1/organizations/{organisationId}")
    ResponseEntity<Organization> getOrganizationById(@PathVariable String organisationId);
}
