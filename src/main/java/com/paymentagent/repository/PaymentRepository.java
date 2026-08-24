package com.paymentagent.repository;

import com.paymentagent.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepository{

    private final List<Payment> payments = new ArrayList<>();

    public PaymentRepository(){

        payments.add(
            new Payment(
                "PAY001",
                5000.00,
                "INR",
                "FAILED",
                "UPI"
            )
        );

        payments.add(
            new Payment(
                "PAY002",
                1200.00,
                "INR",
                "SUCCESS",
                "CARD"
            )
        );

        payments.add(
            new Payment(
                "PAY003",
                8500.00,
                "INR",
                "FAILED",
                "UPI"
            )
        );
    }

    public List<Payment> findAll(){

        return payments;
    }

    public Payment findById(String id){

        for(Payment payment : payments){

            if(payment.getId().equals(id)){
                return payment;
            }
        }
        
        return null;
    }
}