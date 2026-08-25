package com.paymentagent.controller;

import com.paymentagent.model.Payment;
import com.paymentagent.service.PaymentService;
import com.paymentagent.model.PaymentRequest;
import com.paymentagent.model.PaymentStats;

import java.util.List;

public class PaymentController{

    private final PaymentService paymentService;
    
    public PaymentController(){
        paymentService = new PaymentService();
    }

    public List<Payment> getPayments(){
        return paymentService.getPayments();
    }

    public Payment getPaymentById(String id) {

        return paymentService.getPaymentById(id);
    }

    public Payment createPayment(PaymentRequest request){
        return paymentService.createPayment(request);
    }

    public List<Payment> filterPayments(
        String status,
        String paymentMethod,
        Double minAmount,
        Double maxAmount
    ){

        return paymentService.filterPayments(
            status,
            paymentMethod,
            minAmount,
            maxAmount
        );
    }

    public PaymentStats getStats(){
        return paymentService.getStats();
    }
}