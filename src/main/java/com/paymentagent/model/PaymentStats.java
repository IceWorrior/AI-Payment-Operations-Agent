package com.paymentagent.model;

public class PaymentStats{

    private int totalPayments;
    private int successfulPayments;
    private int failedPayments;
    private int pendingPayments;

    private double totalAmount;
    private double failedAmount;
    private double failureRate;

    public PaymentStats(
        int totalPayments,
        int successfulPayments,
        int failedPayments,
        int pendingPayments,
        double totalAmount,
        double failedAmount,
        double failureRate
    ){

        this.totalPayments = totalPayments;
        this.successfulPayments = successfulPayments;
        this.failedPayments = failedPayments;
        this.pendingPayments = pendingPayments;
        this.totalAmount = totalAmount;
        this.failedAmount = failedAmount;
        this.failureRate = failureRate;

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

    public int getPendingPayments(){
        return pendingPayments;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public double getFailedAmount(){
        return failedAmount;
    }

    public double getFailureRate(){
        return failureRate;
    }

}