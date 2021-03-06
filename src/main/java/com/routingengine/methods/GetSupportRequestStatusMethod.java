package com.routingengine.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class GetSupportRequestStatusMethod extends AbstractAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest[] supportRequests = routingEngine.getSupportRequests();
        
        JsonObject supportRequestStatus = new JsonObject();
        int totalSupportRequestCount = 0;
        
        JsonArray closedSupportRequestsArray = new JsonArray();
        int closedSupportRequestCount = 0;
        
        JsonArray openSupportRequestsArray = new JsonArray();
        int openSupportRequestCount = 0;
        
        JsonArray waitingSupportRequestsArray = new JsonArray();
        int waitingSupportRequestCount = 0;
        
        JsonArray assignedSupportRequestsArray = new JsonArray();
        int assignedSupportRequestCount = 0;
        
        for (SupportRequest supportRequest : supportRequests) {
            String supportRequestUUIDString = supportRequest.getUUID().toString();
            
            totalSupportRequestCount++;
            
            if (!supportRequest.isOpen()) {
                closedSupportRequestsArray.add(supportRequestUUIDString);
                closedSupportRequestCount++;
            }
            
            else if (supportRequest.isWaiting()) {
                waitingSupportRequestsArray.add(supportRequestUUIDString);
                waitingSupportRequestCount++;
            }
            
            else if (supportRequest.hasAssignedAgent()) {
                assignedSupportRequestsArray.add(supportRequestUUIDString);
                assignedSupportRequestCount++;
            }
            
            else {
                openSupportRequestsArray.add(supportRequestUUIDString);
                openSupportRequestCount++;
            }
        }
        
        JsonObject closedSupportRequestsStatus = new JsonObject();
        closedSupportRequestsStatus.addProperty("count", closedSupportRequestCount);
        closedSupportRequestsStatus.add("uuids", closedSupportRequestsArray);
        supportRequestStatus.add("closed", closedSupportRequestsStatus);
        
        JsonObject openSupportRequestsStatus = new JsonObject();
        openSupportRequestsStatus.addProperty("count", openSupportRequestCount);
        openSupportRequestsStatus.add("uuids", openSupportRequestsArray);
        supportRequestStatus.add("open", openSupportRequestsStatus);
        
        JsonObject waitingSupportRequestsStatus = new JsonObject();
        waitingSupportRequestsStatus.addProperty("count", waitingSupportRequestCount);
        waitingSupportRequestsStatus.add("uuids", waitingSupportRequestsArray);
        supportRequestStatus.add("waiting", waitingSupportRequestsStatus);
        
        JsonObject assignedSupportRequestsStatus = new JsonObject();
        assignedSupportRequestsStatus.addProperty("count", assignedSupportRequestCount);
        assignedSupportRequestsStatus.add("uuids", assignedSupportRequestsArray);
        supportRequestStatus.add("assigned", assignedSupportRequestsStatus);
        
        supportRequestStatus.addProperty("total", totalSupportRequestCount);
        
        return JsonResponse.success(request, supportRequestStatus);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return true;
    }
}
