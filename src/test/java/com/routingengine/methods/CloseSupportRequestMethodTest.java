package com.routingengine.methods;

import static org.junit.Assume.assumeTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class CloseSupportRequestMethodTest extends AbstractMethodTest
{   
    protected static final String method = "close_support_request";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        final String agentUUIDString = generateNewAgent("rainbow_agent", Map.of(1, true));
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(10);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                closeSupportRequest(supportRequestUUIDString);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                SupportRequest supportRequest = assertResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.isOpen());
                
                assertFalse(supportRequest.hasAssignedAgent());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assertTrue(agent.isAvailable());
                
                assertFalse(agent.hasAssignedSupportRequest());
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid")
    void test02()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid: json array")
    void test03()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid: json object")
    void test04()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid: non-conforming string")
    void test05()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "hahaha test test")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.4 - Invalid uuid: unknown uuid")
    void test06()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "b50544fc-c7db-4c84-ae49-297c3676c796")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing input")
    void test07()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2 - Unexpected arguments")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        final String agentUUIDString = generateNewAgent("rainbow_agent", Map.of(1, true));
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(10);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                SupportRequest supportRequest = assertResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.isOpen());
                
                assertFalse(supportRequest.hasAssignedAgent());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assertTrue(agent.isAvailable());
                
                assertFalse(agent.hasAssignedSupportRequest());
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.1 - Malformed arguments: string")
    void test09()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " \"testtest\"");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.2 - Malformed arguments: empty string")
    void test10()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + "");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.3 - Malformed arguments: numbers")
    void test11()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " 1234");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.4 - Malformed arguments: json array")
    void test12()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " []");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.5 - Malformed arguments: invalid json object")
    void test13()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " {{{{");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
}
