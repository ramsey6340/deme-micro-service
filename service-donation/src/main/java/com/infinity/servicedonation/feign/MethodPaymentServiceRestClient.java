package com.infinity.servicedonation.feign;

import com.infinity.servicedonation.models.MethodPayment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-method-payment")
public interface MethodPaymentServiceRestClient {

    @GetMapping("/api/v1/methodPayments/{methodPaymentId}")
    ResponseEntity<MethodPayment> getMethodPaymentById(@PathVariable String methodPaymentId);
}
