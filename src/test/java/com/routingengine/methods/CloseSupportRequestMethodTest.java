package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.MethodTestBase;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class CloseSupportRequestMethodTest extends MethodTestBase
{   
    protected static final String method = "close_support_request";
    
    @Test
    @DisplayName("Test ~.1 - Missing input")
    void test90()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid missing", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test ~.2 - Unexpected arguments")
    void test91()
        throws IOException, InterruptedException, ExecutionException
    {
    }
    
    @Test
    @DisplayName("Test ~.3.1 - Malformed arguments case 1")
    void test92()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " \"testtest\"");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test ~.3.2 - Malformed arguments case 2")
    void test93()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " []");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test ~.3.3 - Malformed arguments case 3")
    void test94()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " ;!/");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test ~.3.4 - Malformed arguments case 4")
    void test95()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " }}}}");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        customer.run();
    }
}
