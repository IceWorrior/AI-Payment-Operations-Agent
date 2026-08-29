package com.paymentagent.ai;

import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentStats;

import java.util.List;

public class ToolExecutor {
    
    private final PaymentTools tools;

    public ToolExecutor(PaymentTools tools){
        this.tools = tools;
    }

    public Object execute(
        String toolName,
        String status,
        String paymentMethod,
        Double minAmount,
        Double maxAmount
    ){

        switch (toolName) {
            
            case "get_payments":
                    return tools.getPayments();
            
            case "filter_payments":
                    return tools.filterPayments(
                        status, 
                        paymentMethod, 
                        minAmount, 
                        maxAmount);
            
            case "get_payment_stats":
                    return tools.getPaymentStats();
            
            case "get_payment_method_stats":
                    return tools.getPaymentMethodStats();
            
            case "analyze_payment_risk":
                        return tools.analyzePaymentRisk();
            
            default:
                throw new IllegalArgumentException(
                    "Unknow tool: " + toolName
                );
        }

    }

}
