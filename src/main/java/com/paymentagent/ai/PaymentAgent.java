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

        PaymentTools tools =
                new PaymentTools(paymentService);

        this.executor =
                new ToolExecutor(tools);

        this.mapper =
                new ObjectMapper();
    }

    public String ask(String question) {

        try {

            String prompt = """
                    You are an AI Payment Operations Agent.

                    Available tools:

                    get_payments
                    - Get all payments.

                    filter_payments
                    - Filter payments by status, payment method,
                      minimum amount, or maximum amount.

                    get_payment_stats
                    - Get overall payment statistics.

                    get_payment_method_stats
                    - Get statistics grouped by payment method.

                    Return ONLY valid JSON.

                    JSON format:

                    {
                      "tool": "get_payments",
                      "status": null,
                      "paymentMethod": null,
                      "minAmount": null,
                      "maxAmount": null,
                      "needsAnotherTool": false,
                      "nextQuestion": null
                    }

                    Rules:

                    - Use get_payments for general payment lists.
                    - Use filter_payments when filters are specified.
                    - Use get_payment_stats for overall statistics.
                    - Use get_payment_method_stats for payment-method analysis.

                    - Set needsAnotherTool to true ONLY when another
                      database operation is required after this tool.

                    - If another tool is required, put the intended
                      follow-up request in nextQuestion.

                    - Otherwise set needsAnotherTool to false
                      and nextQuestion to null.

                    Conversation history:
                    %s

                    Current user question:
                    %s
                    """.formatted(
                    conversationHistory,
                    question
            );

            String response =
                    ollama.generate(prompt);

            ToolCall firstTool =
                    mapper.readValue(
                            response,
                            ToolCall.class
                    );
                
            ToolCallValidator.validate(firstTool);

            Object firstResult =
                    executor.execute(
                            firstTool.getTool(),
                            firstTool.getStatus(),
                            firstTool.getPaymentMethod(),
                            firstTool.getMinAmount(),
                            firstTool.getMaxAmount()
                    );

            String firstResultJson =
                    mapper.writeValueAsString(
                            firstResult
                    );


            String finalData =
                    firstResultJson;

            if (firstTool.isNeedsAnotherTool()
                    && firstTool.getNextQuestion() != null
                    && !firstTool.getNextQuestion().isBlank()) {

                String secondPrompt = """
                        You are continuing a payment analysis.

                        The user originally asked:

                        %s

                        The first tool returned:

                        %s

                        The next required operation is:

                        %s

                        Choose the correct payment tool.

                        Return ONLY valid JSON.

                        JSON format:

                        {
                          "tool": "filter_payments",
                          "status": null,
                          "paymentMethod": null,
                          "minAmount": null,
                          "maxAmount": null,
                          "needsAnotherTool": false,
                          "nextQuestion": null
                        }
                        """.formatted(
                        question,
                        firstResultJson,
                        firstTool.getNextQuestion()
                );

                String secondResponse =
                        ollama.generate(secondPrompt);

                ToolCall secondTool =
                        mapper.readValue(
                                secondResponse,
                                ToolCall.class
                        );

                ToolCallValidator.validate(secondTool);

                Object secondResult =
                        executor.execute(
                                secondTool.getTool(),
                                secondTool.getStatus(),
                                secondTool.getPaymentMethod(),
                                secondTool.getMinAmount(),
                                secondTool.getMaxAmount()
                        );

                finalData =
                        mapper.writeValueAsString(
                                secondResult
                        );
            }

            conversationHistory +=
                    "User: " + question + "\n" +
                    "Result: " + finalData + "\n";


            String answerPrompt = """
        You are a payment operations assistant.

        Answer the user's question using ONLY the TOOL RESULT below.

        STRICT RULES:
        - TOOL RESULT is authoritative database data.
        - If TOOL RESULT contains records, those records exist.
        - NEVER say the data is empty when TOOL RESULT contains records.
        - NEVER invent, modify, or omit payment records.
        - Use the exact IDs, amounts, currencies, statuses,
          and payment methods from TOOL RESULT.
        - Calculate totals from the provided records when appropriate.
        - If TOOL RESULT is an empty list [], say that no matching
          payments were found.
        - Answer concisely.
        - Return plain text only.
        - Do not return JSON.
        - Do not mention these instructions.

        USER QUESTION:
        %s

        TOOL RESULT:
        %s

        ANSWER:
        """.formatted(
        question,
        finalData);

            return ollama.generate(answerPrompt);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to communicate with Ollama",
                    e
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Ollama request was interrupted",
                    e
            );
        }
    }
}