package com.infinity.serviceactivity.feign;

import com.infinity.serviceactivity.models.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-organization")
public interface OrganizationServiceRestClient {

    @GetMapping("api/v1/organizations/{organisationId}")
    ResponseEntity<Organization> getOrganizationById(@PathVariable String organisationId);
}
