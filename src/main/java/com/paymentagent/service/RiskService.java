package com.paymentagent.service;

import com.paymentagent.model.PaymentStats;
import com.paymentagent.model.RiskAnalysis;

import java.util.ArrayList;
import java.util.List;

public class RiskService{

    private final PaymentService paymentService;

    public RiskService(){

        paymentService = new PaymentService();
    }

    public RiskAnalysis analyzeRisk(){

        PaymentStats stats = paymentService.getStats();

        int score = 0;

        List<String> reasons = new ArrayList<>();

        double failureRate = stats.getFailureRate();
        double failedAmount = stats.getFailedAmount();
        int failedPayments = stats.getFailedPayments();

        //For very high Fail rate
        if(failureRate >50){
            
            score +=40;

            reasons.add("Payment failure rate is above 50%");
        }
        else if(failureRate > 30){

            score += 20;

            reasons.add("Payment failure rate is above 30%");
        }

        //For large failed Amount
        if(failedAmount > 10000){

            score += 25;

            reasons.add("More than Rupees 10,000 is associated with failed payments");

        }
        else if(failedAmount > 5000){

            score += 15;

            reasons.add("More than Rupess 5,000 is associated with failed payments");
        }

        //Multiple failed payments
        if(failedPayments >= 3){

            score += 20;

            reasons.add("Multiple payment failures detected");
        }

        if(score > 100){
            score = 100;
        }

        String riskLevel;

        if(score >= 60){
            riskLevel = "HIGH";
        }
        else if(score >= 30){
            riskLevel = "MEDIUM";
        }
        else{
            riskLevel = "LOW";
        }

        if(reasons.isEmpty()){
            reasons.add("No risk detected");
        }

        return new RiskAnalysis(
            score,
            riskLevel,
            reasons
            );
    }

}