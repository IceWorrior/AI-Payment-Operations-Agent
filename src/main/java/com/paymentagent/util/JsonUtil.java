package com.paymentagent.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil{

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object){

        try{

            return mapper.writeValueAsString(object);

        }
        catch(JsonProcessingException e){

            throw new RuntimeException("Failed to convert object to json", e);
        }
    }
}