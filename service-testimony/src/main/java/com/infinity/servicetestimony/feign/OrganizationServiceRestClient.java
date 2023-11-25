package com.infinity.servicetestimony.feign;

import com.infinity.servicetestimony.models.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "service-organization")
public interface OrganizationServiceRestClient {

    @GetMapping("api/v1/organizations/{organisationId}")
    ResponseEntity<Organization> getOrganizationById(@PathVariable String organisationId);
}
