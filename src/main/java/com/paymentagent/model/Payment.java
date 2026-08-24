package com.paymentagent.model;

public class Payment{

    private String id;
    private double amount;
    private String currency;
    private String status;
    private String paymentMethod;

    public Payment(String id, double amount, String currency, String status, String paymentMethod){

        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public String getId(){
        return id;
    }

    public double getAmount(){
        return amount;
    }

    public String getCurrency(){
        return currency;
    }

    public String getStatus(){
        return status;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }
}