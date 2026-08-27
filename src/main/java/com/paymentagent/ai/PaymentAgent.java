package com.paymentagent.ai;

import com.paymentagent.model.Payment;
import com.paymentagent.service.PaymentService;

import java.io.IOException;
import java.util.List;

public class PaymentAgent {

    private final OllamaClient ollama;
    private final PaymentTools tools;

    public PaymentAgent(
            OllamaClient ollama,
            PaymentService paymentService) {
        this.ollama = ollama;
        this.tools = new PaymentTools(paymentService);
    }

    public String ask(String question) {

        String prompt = """
                You are an AI Payment Operations Agent.

                You have access to these tools:

                1. get_payments
                   Get all payments.

                2. filter_payments
                   Filter payments by:
                   - status
                   - payment method
                   - minimum amount
                   - maximum amount

                3. get_payment_stats
                   Get payment statistics.

                User question:
                %s

                Decide what information is needed to answer the question.
                """.formatted(question);

        try {
            return ollama.generate(prompt);
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
