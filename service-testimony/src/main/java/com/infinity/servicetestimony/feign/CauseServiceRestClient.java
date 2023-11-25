package com.infinity.servicetestimony.feign;

import com.infinity.servicetestimony.models.Cause;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-cause")
public interface CauseServiceRestClient {

    @GetMapping("/api/v1/causes/{causeId}")
    ResponseEntity<Cause> getCauseById(@PathVariable String causeId);
}
