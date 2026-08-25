package com.paymentagent.ai;

public class OllamaTest{

    public static void main(String args[]){

        try {

            OllamaClient ollama = new OllamaClient();

            String response = ollama.generate("Explain what a failed payment is in one short sentence.");

            System.out.println(response);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}