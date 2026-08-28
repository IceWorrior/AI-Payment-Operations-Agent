package com.paymentagent.server;

import com.paymentagent.controller.PaymentController;
import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentRequest;
import com.paymentagent.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.paymentagent.model.PaymentStats;
import com.paymentagent.model.RiskAnalysis;
import com.paymentagent.service.RiskService;
import com.paymentagent.ai.OllamaClient;
import com.paymentagent.ai.PaymentAgent;
import com.paymentagent.service.PaymentService;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Router {

    private final PaymentController paymentController;
    private final PaymentAgent paymentAgent;
    private final RiskService riskService;

    public Router() {

        paymentController = new PaymentController();
        paymentAgent = new PaymentAgent(
                new OllamaClient(),
                new PaymentService()
                );
        riskService = new RiskService();
    }

    public void registerRoutes(
            com.sun.net.httpserver.HttpServer server) {

        server.createContext(
                "/api/payments/filter",
                this::handleFilterPayments
        );

        server.createContext(
            "/api/payments/stats",
            this::handlePaymentStats
        );

        server.createContext(
            "/api/payments/risk",
            this::handleRiskAnalysis
        );

        server.createContext(
                "/api/payments",
                this::handlePayments
        );

        server.createContext(
                "/api/payments/",
                this::handlePaymentById
        );

        server.createContext(
        "/api/ai",
        this::handleAI
        );

    }

    private void handlePayments(
            HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {

            List<Payment> payments =
                    paymentController.getPayments();

            sendResponse(
                    exchange,
                    200,
                    JsonUtil.toJson(payments)
            );

            return;
        }

        if (method.equals("POST")) {

            handleCreatePayment(exchange);

            return;
        }

        sendResponse(
                exchange,
                405,
                "{\"error\":\"Method not allowed\"}"
        );
    }

    private void handlePaymentById(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method not allowed\"}"
            );

            return;
        }

        String path =
                exchange.getRequestURI().getPath();

        String id =
                path.substring("/api/payments/".length());

        Payment payment =
                paymentController.getPaymentById(id);

        if (payment == null) {

            sendResponse(
                    exchange,
                    404,
                    "{\"error\":\"Payment not found\"}"
            );

            return;
        }

        sendResponse(
                exchange,
                200,
                JsonUtil.toJson(payment)
        );
    }

    private void handleCreatePayment(
            HttpExchange exchange) throws IOException {

        try {

            String requestBody =
                    new String(
                            exchange.getRequestBody()
                                    .readAllBytes()
                    );

            PaymentRequest request =
                    JsonUtil.fromJson(
                            requestBody,
                            PaymentRequest.class
                    );

            Payment payment =
                    paymentController.createPayment(request);

            sendResponse(
                    exchange,
                    201,
                    JsonUtil.toJson(payment)
            );

        } catch (IllegalArgumentException e) {

            String response =
                    "{\"error\":\"" +
                    e.getMessage() +
                    "\"}";

            sendResponse(
                    exchange,
                    400,
                    response
            );
        }
    }

    private void handleFilterPayments(
        HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method not allowed\"}"
            );

            return;
        }

        String query =
                exchange.getRequestURI()
                        .getQuery();

        String status = null;
        String paymentMethod = null;
        Double minAmount = null;
        Double maxAmount = null;

        if (query != null && !query.isBlank()) {

            String[] parameters =
                    query.split("&");

            for (String parameter : parameters) {

                String[] pair =
                        parameter.split("=", 2);

                if (pair.length != 2) {
                    continue;
                }

                String key = pair[0];
                String value = pair[1];

                if (value.isBlank()) {
                    continue;
                }

                switch (key) {

                    case "status":
                        status = value;
                        break;

                    case "paymentMethod":
                        paymentMethod = value;
                        break;

                    case "minAmount":

                        minAmount =
                                Double.valueOf(value);

                        break;

                    case "maxAmount":

                        maxAmount =
                                Double.valueOf(value);

                        break;

                    default:
                        break;
                }
            }
        }

        List<Payment> payments =
                paymentController.filterPayments(
                        status,
                        paymentMethod,
                        minAmount,
                        maxAmount
                );

        sendResponse(
                exchange,
                200,
                JsonUtil.toJson(payments)
        );
    }

    private void sendResponse(
        HttpExchange exchange,
        int statusCode,
        String response) throws IOException {

        byte[] responseBytes =
                response.getBytes();

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(responseBytes);

        exchange.close();
    }

    private void handlePaymentStats(
        HttpExchange exchange
    ) throws IOException{

        if(!exchange.getRequestMethod().equals("GET")){

            sendResponse(
                exchange,
                405,
                "{\"error\":\"Method not allowed\"}"
            );
            return;
        }

        PaymentStats stats = paymentController.getStats();

        sendResponse(
            exchange,
            200,
            JsonUtil.toJson(stats)
        );

    }

    private void handleRiskAnalysis(
        HttpExchange exchange
    ) throws IOException{

        if(!exchange.getRequestMethod().equals("GET")){

            sendResponse(
                exchange,
                405,
                "{\"erro\":\"Method not allowed\"}"
            );

            return;

        }

        RiskAnalysis analysis = riskService.analyzeRisk();

        sendResponse(
            exchange,
            200,
            JsonUtil.toJson(analysis)
        );

    }

    private void handleAI(HttpExchange exchange) throws IOException{

        if(!exchange.getRequestMethod().equalsIgnoreCase("POST")){

                sendResponse(
                        exchange,
                        405,
                        "{\"error:\":\"Method not allowed\"}"
                );

                return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes()
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(body);
        String question = json.get("question").asText();
        String result = paymentAgent.ask(question);

        sendResponse(
                exchange,
                200,
                result
        );

    }
}