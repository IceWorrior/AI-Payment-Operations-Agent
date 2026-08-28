package com.paymentagent.ai;

public class ToolCall {
    
    private String tool;
    private String status;
    private String paymentMethod;
    private Double minAmount;
    private Double maxAmount;

    public ToolCall(){

    }

    public String getTool(){
        return tool;
    }

    public void setTool(String tool){
        this.tool = tool;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public Double getMinAmount(){
        return minAmount;
    }

    public void setMinAmount(Double minAmount){
        this.minAmount = minAmount;
    }

    public Double getMaxAmount(){
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount){
        this.maxAmount = maxAmount;
    }

}
