package com.paymentagent.service;

import com.paymentagent.model.Payment;
import com.paymentagent.repository.PaymentRepository;
import com.paymentagent.model.PaymentRequest;
import com.paymentagent.validation.PaymentValidator;

import java.util.List;
import java.util.UUID;

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

        String id = "PAY-" +
            UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

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