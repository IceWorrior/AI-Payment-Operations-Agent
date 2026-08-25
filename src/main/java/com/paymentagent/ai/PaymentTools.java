package com.paymentagent.ai;

import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentStats;
import com.paymentagent.model.RiskAnalysis;
import com.paymentagent.service.PaymentService;
import com.paymentagent.service.RiskService;

import java.util.List;

public class PaymentTools{

    private final PaymentService paymentService;
    private final RiskService riskService;

    public PaymentTools(){

        paymentService = new PaymentService();
        riskService = new RiskService();

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

    public PaymentStats getPaymentStats(){
        return paymentService.getStats();
    }

    public RiskAnalysis analyzePaymentRisk(){
        return riskService.analyzeRisk();
    }

}