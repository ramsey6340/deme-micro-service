package com.infinity.servicecause.feign;

import com.infinity.servicecause.models.Admin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-admin")
public interface AdminServiceRestClient {

    @PostMapping("api/v1/admins/{adminId}")
    ResponseEntity<Admin> getAdminById(@PathVariable String adminId);
}
