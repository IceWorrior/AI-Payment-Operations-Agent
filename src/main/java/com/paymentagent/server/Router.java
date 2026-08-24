package com.paymentagent.server;

import com.paymentagent.controller.PaymentController;
import com.paymentagent.model.Payment;
import com.paymentagent.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class Router {

    private final PaymentController paymentController;

    public Router() {

        paymentController = new PaymentController();
    }

    public void registerRoutes(
            com.sun.net.httpserver.HttpServer server) {

        server.createContext(
                "/api/payments",
                this::handlePayments
        );

        server.createContext(
                "/api/payments/",
                this::handlePaymentById
        );
    }

    private void handlePayments(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equals("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method not allowed\"}"
            );

            return;
        }

        List<Payment> payments =
                paymentController.getPayments();

        sendResponse(
                exchange,
                200,
                JsonUtil.toJson(payments)
        );
    }

    private void handlePaymentById(HttpExchange exchange)
            throws IOException {

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

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response) throws IOException {

        byte[] responseBytes =
                response.getBytes();

        exchange.getResponseHeaders()
                .set("Content-Type", "application/json");

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(responseBytes);

        exchange.close();
    }
}