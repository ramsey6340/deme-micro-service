package com.infinity.servicetestimony.feign;

import com.infinity.servicetestimony.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-user")
public interface UserServiceRestClient {

    @GetMapping("/api/v1/users/{userId}")
    ResponseEntity<User> getUserById(@PathVariable String userId);
}
