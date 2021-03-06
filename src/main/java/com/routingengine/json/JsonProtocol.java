package com.routingengine.json;

import static com.routingengine.json.JsonUtils.getAsInt;
import static com.routingengine.json.JsonUtils.getAsString;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;


public class JsonProtocol
{
    public static final String EXIT_COMMAND = "exit";
    
    private JsonProtocol()
    {
        throw new UnsupportedOperationException("do not instantiate!");
    }
    
    public static JsonRequest readJsonRequest(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        JsonRequest jsonRequest = new JsonRequest();
        
        readJsonRequest(jsonReader, jsonRequest);
        
        return jsonRequest;
    }
    
    public static void readJsonRequest(JsonReader jsonReader, JsonRequest jsonRequest)
        throws IOException, JsonProtocolException
    {
        try {
            String method = jsonReader.readString();
            
            if (EXIT_COMMAND.equals(method))
                throw new ExitConnectionException();
            
            jsonRequest.setMethod(method);
        }
        
        catch (IllegalStateException | MalformedJsonException exception) {
            jsonReader.initializeReader();
            
            throw new JsonProtocolException("malformed request");
        }
        
        try {
            JsonObject arguments = jsonReader.parseJsonObject();
            
            jsonRequest.setArguments(arguments);
            
            jsonReader.initializeReader();
        }
        
        catch (IllegalArgumentException | JsonParseException exception) {
            jsonReader.initializeReader();
            
            throw new JsonProtocolException("malformed arguments");
        }
        
        jsonRequest.ensureWellFormed();
    }
    
    public static JsonResponse readJsonResponse(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        JsonResponse jsonResponse = new JsonResponse();
        
        readJsonResponse(jsonReader, jsonResponse);
        
        return jsonResponse;
    }
    
    public static void readJsonResponse(JsonReader jsonReader, JsonResponse jsonResponse)
        throws IOException, JsonProtocolException
    {
        JsonObject response;
        
        try {
            response = jsonReader.parseJsonObject();
            
            jsonReader.initializeReader();
        }
        
        catch (IllegalArgumentException | JsonParseException exception) {
            jsonReader.initializeReader();
            
            throw new JsonProtocolException("malformed response");
        }
        
        jsonResponse
            .setTicketNumber(getAsInt(response, "ticket_number"))
            .setMethod(getAsString(response, "method"))
            .setResult(getAsString(response, "result"))
            .setPayload(response.get("payload"));
        
        jsonResponse.ensureWellFormed();
    }
    
    public static void writeJsonRequest(JsonRequest jsonRequest, JsonWriter jsonWriter)
        throws IOException, JsonProtocolException
    {
        jsonRequest.ensureWellFormed();
        
        jsonWriter.initializeBuffer();
        
        String method = jsonRequest.getMethod();
        
        jsonWriter.writeString(method + " ");
        
        JsonObject arguments = jsonRequest.getArguments();
        
        jsonWriter.writeJsonObject(arguments);
        
        jsonWriter.flush();
    }
    
    public static void writeJsonResponse(JsonResponse jsonResponse, JsonWriter jsonWriter)
        throws IOException, JsonProtocolException
    {
        jsonResponse.ensureWellFormed();
        
        jsonWriter.initializeBuffer();
        
        JsonObject response = jsonResponse.toJson();
        
        jsonWriter.writeJsonObject(response);
        
        jsonWriter.flush();
    }
    
    public static class ExitConnectionException extends IOException
    {
        public ExitConnectionException()
        {
            super();
        }
        
        public ExitConnectionException(String message)
        {
            super(message);
        }
    }
    
    public static class JsonProtocolException extends Exception
    {
        public JsonProtocolException(String message)
        {
            super(message);
        }
    }
}
