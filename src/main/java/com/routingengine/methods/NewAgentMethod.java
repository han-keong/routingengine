package com.routingengine.methods;

import static com.routingengine.MethodManager.Method;
import static com.routingengine.json.JsonUtils.getAsBooleanMap;
import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class NewAgentMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = newAgent(arguments);
        
        addAgent(agent);
        
        return agent.toJson();
    }
    
    public Agent newAgent(JsonObject arguments)
    {
        return Agent.builder()
            .setAddress(getAsString(arguments, "address"))
            .setSkills(getAsBooleanMap(arguments, "skills"))
            .build();
    }
    
    public void addAgent(Agent agent)
    {
        routingEngine.addAgent(agent);
    }
}