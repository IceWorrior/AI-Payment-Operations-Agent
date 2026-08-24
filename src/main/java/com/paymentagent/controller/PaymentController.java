package com.paymentagent.controller;

import com.paymentagent.model.Payment;
import com.paymentagent.service.PaymentService;

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
}