package com.paymentagent.service;

import com.paymentagent.model.Payment;
import com.paymentagent.repository.PaymentRepository;

import java.util.List;

public class PaymentService{

    private final PaymentRepository paymentRepository;

    public PaymentService(){

        paymentRepository = new PaymentRepository();
    }

    public List<Payment> getPayments(){

        return paymentRepository.findAll();
    }

    public Payment getPaymentById(String id){
        return paymentRepository.findById(id);
    }
}