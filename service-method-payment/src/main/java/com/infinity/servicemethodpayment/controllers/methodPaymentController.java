package com.infinity.servicemethodpayment.controllers;

import com.infinity.servicemethodpayment.models.MethodPayment;
import com.infinity.servicemethodpayment.services.MethodPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/methodPayments/")
public class methodPaymentController {

    @Autowired
    private MethodPaymentService methodPaymentService;

    @GetMapping("")
    @Operation(summary = "Récuperer la liste des méthodes de paiement")
    public List<MethodPayment> getAllMethodPayment(){
        return methodPaymentService.getAllMethodPayment();
    }

    @GetMapping("{methodPaymentId}")
    @Operation(summary = "Récuperer une methode de paiement par son ID")
    public ResponseEntity<MethodPayment> getMethodPaymentById(@PathVariable String methodPaymentId) {
        return methodPaymentService.getMethodPaymentById(methodPaymentId);
    }
}
