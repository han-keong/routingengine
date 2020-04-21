package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.InetEntity;
import com.routingengine.RoutingEngine;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public abstract class AbstractMethod
    implements Method
{
    protected RoutingEngine routingEngine;
    
    @Override
    public void setRoutingEngine(RoutingEngine routingEngine)
    {
        this.routingEngine = routingEngine;
    }
    
    @Override
    public final JsonResponse handleRequest(JsonRequest request)
    {
        beforeHandle(request);
        
        JsonResponse response = handle(request);
        
        afterHandle(request, response);
        
        return response;
    }
    
    protected void beforeHandle(JsonRequest request) { }
    
    protected abstract JsonResponse handle(JsonRequest request);
    
    protected void afterHandle(JsonRequest request, JsonResponse response) { }
    
    protected static final void ensureMissingException(IllegalArgumentException exception)
    {
        String message = exception.getMessage();
        
        if (!message.contains("missing"))
            throw exception;
    }
    
    protected static final void updateAddress(InetEntity entity, JsonRequest request)
    {
        String address = getAsString(request, "address");
        
        entity.setAddress(address);
    }
}
