package com.paymentagent.ai;

public class ToolCall {
    
    private String tool;
    private String status;
    private String paymentMethod;
    private Double minAmount;
    private Double maxAmount;
    private boolean needsAnotherTool;
    private String nextQuestion;

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

    public boolean isNeedsAnotherTool(){
        return needsAnotherTool;
    }

    public void setNeedsAnotherTool(boolean needsAnotherTool){
        this.needsAnotherTool = needsAnotherTool;
    }

    public String getNextQuestion(){
        return nextQuestion;
    }

    public void setNextQuestion(String nexQuestion){
        this.nextQuestion = nextQuestion;
    }

}
