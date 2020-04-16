package com.routingengine.websocket;

import java.io.InputStream;
import java.io.Reader;
import com.routingengine.json.JsonReader;


public class WebSocketJsonReader extends JsonReader
{
    private boolean masked;
    
    public WebSocketJsonReader(InputStream inputStream)
    {
        super(inputStream);
    }
    
    public void setMasked(boolean masked)
    {
        this.masked = masked;
        
        initializeReader();
    }
    
    @Override
    protected Reader getReader(InputStream inputStream)
    {
        WebSocketStreamReader reader = new WebSocketStreamReader(inputStream);
        
        reader.setMasked(masked);
        
        return (Reader) reader;
    }
}