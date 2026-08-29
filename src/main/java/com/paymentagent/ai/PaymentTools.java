package com.paymentagent.ai;

import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentStats;
import com.paymentagent.service.PaymentService;
import com.paymentagent.model.PaymentMethodStats;
import com.paymentagent.model.RiskAnalysis;
import com.paymentagent.service.RiskService;


import java.util.List;

public class PaymentTools{

    private final PaymentService paymentService;
    private final RiskService riskService;
    
    public PaymentTools(PaymentService paymentService){
        this.paymentService = paymentService;
        this.riskService = new RiskService();
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

    public List<PaymentMethodStats> getPaymentMethodStats(){
        return paymentService.getPaymentMethodStats();
    }

    public RiskAnalysis analyzePaymentRisk(){
        return riskService.analyzeRisk();
    }

}