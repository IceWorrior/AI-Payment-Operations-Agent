package com.paymentagent.ai;

import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentStats;
import com.paymentagent.service.PaymentService;

import java.util.List;

public class PaymentTools{

    private final PaymentService paymentService;
    
    public PaymentTools(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public List<Payment> getPayments(){
        return paymentService.getPayments();
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

    public Object getPaymentStats(){
        return paymentService.getStats();
    }

}