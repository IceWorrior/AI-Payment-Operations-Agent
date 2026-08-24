package com.paymentagent.model;

public class PaymentRequest{

    private double amount;
    private String currency;
    private String paymentMethod;

    public PaymentRequest(){

    }

    public double getAmount(){
        return amount;
    }

    public String getCurrency(){
        return currency;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }

    public void setCurrency(String currency){
        this.currency = currency;
    }

    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }


}