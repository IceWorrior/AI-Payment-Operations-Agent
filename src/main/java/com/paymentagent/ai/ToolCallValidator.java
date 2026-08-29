package com.paymentagent.ai;

public class ToolCallValidator {
    
    public static void validate(ToolCall toolCall){

        if(toolCall == null){
            throw new IllegalArgumentException(
                "Tool call cannot be null."
            );
        }

        String tool = toolCall.getTool();

        if(tool == null || tool.isBlank()){
            throw new IllegalArgumentException(
                "Tool name is required."
            );
        }

        switch(tool){

            case "get_payments":
            case "get_payment_stats":
            case "get_payment_method_stats":
            case "analyze_payment_risk":
                break;
            
            case "filter_payments":
                validateFilterTool(toolCall);
                break;
            
            default:
                throw new IllegalArgumentException(
                    "unknown tool: " + tool
                );

        }

    }

    private static void validateFilterTool(ToolCall toolCall){

        String status = toolCall.getStatus();

        String paymentMethod = toolCall.getPaymentMethod();

        Double minAmount = toolCall.getMinAmount();

        Double maxAmount = toolCall.getMaxAmount();

        if(status != null && !status.isBlank()){

            status = status.trim().toUpperCase();

            if(
                !status.equals("SUCCESS") &&
                !status.equals("FAILED") &&
                !status.equals("PENDING")
            ){

                throw new IllegalArgumentException(
                    "Invalid status: " + paymentMethod
                );

            }

        }

        if (
            maxAmount != null &&
            maxAmount < 0
        ){

            throw new IllegalArgumentException(
                "Maximum Amount cannot be negative."
            );
        }

        if(
            minAmount != null &&
            maxAmount != null &&
            minAmount >maxAmount
        ){

            throw new IllegalArgumentException(
                "Minimum Amount cannot be greater than maximum amount"
            );

        }

    }

}
