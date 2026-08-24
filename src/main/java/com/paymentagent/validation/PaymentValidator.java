package com.paymentagent.validation;

import com.paymentagent.model.PaymentRequest;

public class PaymentValidator{

    public static String validate(PaymentRequest request){

        if(request == null){
            return "Request body is required";
        }

        if(request.getAmount() <= 0){
            return "Amount must be greater than 0";
        }

        if(request.getCurrency() == null ||
            request.getCurrency().isBlank()){
                return "Currency is required";
        }

        if(request.getPaymentMethod() == null ||
            request.getPaymentMethod().isBlank()){
                return "Payment Method is required";
        }

        if(!request.getCurrency().equals("INR")){
            return "Only INR is supported";
        }

        if(!request.getPaymentMethod().equals("UPI") && 
            !request.getPaymentMethod().equals("CARD")){
                return "Payment medthod must be UPI or CARD";
        }

        return null;
    }
}