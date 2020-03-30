package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodTestBase;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class TakeSupportRequestMethodTest extends MethodTestBase
{   
    protected static final String method = "take_support_request";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = takeSupportRequest(agentUUIDString);
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                Agent agent = Agent.fromJson(payload);
                
                assertTrue(agent.hasAssignedSupportRequest());
            }
        });
        
        agent.run();
        
        assertTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        removeSupportRequest(supportRequestUUIDString);
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid")
    void test02()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid case 1")
    void test03()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid case 2")
    void test04()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid case 3")
    void test05()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "hahaha test test")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing input")
    void test06()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.2 - Unexpected arguments")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                Agent agent = Agent.fromJson(payload);
                
                assertTrue(agent.hasAssignedSupportRequest());
            }
        });
        
        agent.run();
        
        assertTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        removeSupportRequest(supportRequestUUIDString);
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.1 - Malformed arguments case 1")
    void test08()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.3.2 - Malformed arguments case 2")
    void test09()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.3.3 - Malformed arguments case 3")
    void test10()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.3.4 - Malformed arguments case 4")
    void test11()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
        
        agent.run();
    }
}
