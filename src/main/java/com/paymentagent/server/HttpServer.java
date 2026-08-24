package com.paymentagent.server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServer{

    private final com.sun.net.httpserver.HttpServer server;
    

    public HttpServer() throws IOException{

        server = com.sun.net.httpserver.HttpServer.create(
            new InetSocketAddress(8000),
            0
        );

        Router router = new Router();
        router.registerRoutes(server);

    }

    public void start(){
        server.start();
        System.out.println("Server running on http://localhost:8000");
    }
}