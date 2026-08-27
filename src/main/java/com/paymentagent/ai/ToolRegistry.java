package com.paymentagent.ai;

import java.util.ArrayList;
import java.util.List;

public class ToolRegistry {
    
    private final List<ToolDefinition> tools = new ArrayList<>();

    public ToolRegistry(){

        tools.add(
            new ToolDefinition(
                "get_payments",
                "Get a list of payments from the payment database."
                )
        );

        tools.add(
            new ToolDefinition(
                "filter_payments",
                "Filter payments by status, payment method, minimum amount, or maximum."
                )
        );

        tools.add(
            new ToolDefinition(
                "get_payment_stats",
                "Get payment statistics such as total payments, successful payments,failed payments, and amounts."
                )
        );
    }

    public List<ToolDefinition> getTools(){
        return tools;
    }

}
