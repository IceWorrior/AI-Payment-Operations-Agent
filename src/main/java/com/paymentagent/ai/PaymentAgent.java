package com.paymentagent.ai;

import com.paymentagent.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PaymentAgent {

    private final OllamaClient ollama;
    private final ToolExecutor executor;
    private final ObjectMapper mapper;
    private String conversationHistory = "";

    public PaymentAgent(
            OllamaClient ollama,
            PaymentService paymentService) {
        this.ollama = ollama;

        PaymentTools tools = new PaymentTools(paymentService);

        this.executor = new ToolExecutor(tools);
        this.mapper = new ObjectMapper();
    }

    public String ask(String question) {

        String prompt = """
                You are an AI Payment Operations Agent.

                You can use these tools:

                get_payments
                - Get all payments.

                filter_payments
                - Filter payments by status, payment method,
                  minimum amount, or maximum amount.

                get_payment_stats
                - Get payment statistics.

                Return ONLY valid JSON.
                Do not use markdown.
                Do not explain anything.

                JSON format:

                {
                  "tool": "get_payments",
                  "status": null,
                  "paymentMethod": null,
                  "minAmount": null,
                  "maxAmount": null
                }

                Rules:

                - Use get_payments when the user wants a list of payments.
                - Use filter_payments when the user specifies filters.
                - Use get_payment_stats when the user asks for statistics.
                - Use null for filters that were not specified.
                - Use the conversation history to understand follow-up questions.
                - If the user adds a filter to the previous request, preserve
                  the previous filters unless the user changes them.

                Conversation history:
                %s

                Current user question:
                %s
                """.formatted(
                conversationHistory,
                question);

        try {

            String response = ollama.generate(prompt);

            ToolCall toolCall = mapper.readValue(response, ToolCall.class);

            Object result = executor.execute(
                    toolCall.getTool(),
                    toolCall.getStatus(),
                    toolCall.getPaymentMethod(),
                    toolCall.getMinAmount(),
                    toolCall.getMaxAmount());

            String resultJson = mapper.writeValueAsString(result);
            conversationHistory += "User: " + question + "\n" +
                                    "Result: " + resultJson + "\n";

            String answerPrompt = """
                    You are an AI Payment Operations Agent.

                    Answer the user's question using ONLY the payment data
                    provided below.

                    IMPORTANT:
                    - The payment data below is authoritative.
                    - Do NOT claim the data is empty if records are present.
                    - Do NOT invent any payments.
                    - Do NOT ignore the payment data.
                    - If there are matching payments, summarize them.
                    - Calculate totals when useful.
                    - Be concise.
                    - Return plain text only.

                    Conversation history:
                    %s

                    User question:
                    %s

                    Payment data:
                    %s

                    Now answer the user's question using the payment data.
                    """.formatted(
                    conversationHistory,
                    question,
                    resultJson);

            return ollama.generate(answerPrompt);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to communicate with Ollama",
                    e);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Ollama request was interrupted",
                    e);
        }

    }

}
