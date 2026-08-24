package com.paymentagent.service;

import com.paymentagent.model.Payment;
import com.paymentagent.repository.PaymentRepository;
import com.paymentagent.model.PaymentRequest;
import com.paymentagent.validation.PaymentValidator;

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

    public Payment createPayment(PaymentRequest request){

        String validationError = PaymentValidator.validate(request);

        if(validationError != null){
            throw new IllegalArgumentException(validationError);
        }

        String id = "PAY" + String.format(
            "%03d",
            paymentRepository.findAll().size() + 1
        );

        Payment payment = new Payment(
            id,
            request.getAmount(),
            request.getCurrency(),
            "PENDING",
            request.getPaymentMethod()
        );

        return paymentRepository.save(payment);
    }
}