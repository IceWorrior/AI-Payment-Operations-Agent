package com.paymentagent;

import com.paymentagent.server.HttpServer;

public class Main{

    public static void main(String agrs[]){

        System.out.println("AI payment agent is starting....");

        try{
            HttpServer server = new HttpServer();
            server.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}