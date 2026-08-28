package com.paymentagent.model;

public class PaymentMethodStats {
    
    private String paymentMethod;
    private int totalPayments;
    private int successfulPayments;
    private int failedPayments;
    private double totalAmount;
    private double failedAmount;

    public PaymentMethodStats(
        String paymentMethod,
        int totalPayments,
        int successfulPayments,
        int failedPayments,
        double totalAmount,
        double failedAmount
    ){

        this.paymentMethod = paymentMethod;
        this.totalPayments = totalPayments;
        this.successfulPayments = successfulPayments;
        this.failedPayments = failedPayments;
        this.totalAmount=  totalAmount;
        this.failedAmount = failedAmount;

    }

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public int getTotalPayments(){
        return totalPayments;
    }

    public int getSuccessfulPayments(){
        return successfulPayments;
    }

    public int getFailedPayments(){
        return failedPayments;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public double getFailedAmount(){
        return failedAmount;
    }

}
