package com.routingengine.server;

import static com.routingengine.Logger.log;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.routingengine.RoutingEngine;


public final class Server
    implements Runnable, Closeable
{
    private final InetSocketAddress address;
    private final RoutingEngine routingEngine;
    private final ExecutorService executorService;
    private final static int THREAD_POOL_SIZE = 1000;
    private final static int SOCKET_TIMEOUT = 1000;
    private final static int SHUTDOWN_TIMEOUT = 10000;
    
    public Server(String hostname, int port)
    {
        address = new InetSocketAddress(hostname, port);
        
        routingEngine = new RoutingEngine();
        
        executorService = newFixedThreadPool(THREAD_POOL_SIZE);
    }
    
    @Override
    public final void run()
    {
        try (ServerSocket listener = new ServerSocket()) {
            
            listener.bind(address);
            
            log("Server bound to " + address);
            
            listener.setSoTimeout(SOCKET_TIMEOUT);
            
            log("Server listening to " + listener);
            
            Socket socket = null;
            
            while (true) {
                
                try {
                    socket = listener.accept();
                    
                    ServerConnectionHandler connectionHandler =
                        new ServerConnectionHandler(socket, routingEngine);
                    
                    executorService.execute(connectionHandler);
                }
                
                catch (SocketTimeoutException exception) {
                    log(".");
                }
                
                catch (IOException exception) {
                    log("I/O exception: " + socket);
                }
                
                if (Thread.currentThread().isInterrupted()) {
                    log("Server interrupted");
                    
                    break;
                }
            }
        }
        
        catch (Exception exception) {
            // server crashed! what happened?
            
            exception.printStackTrace();
        }
        
        finally {
            close();
        }
    }
    
    @Override
    public final void close()
    {
        if (executorService.isTerminated()) {
            log("Server has already been closed");
            
            return;
        }
        
        log("Closing server");
        
        executorService.shutdown();
        
        log("Waiting for server to shut down...");
        
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)) {
                
                executorService.shutdownNow();
                
                log("Waiting for server to shut down...");
                
                if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS))
                    return;
            }
        }
        
        catch (InterruptedException exception) {
            executorService.shutdownNow();
            
            Thread.currentThread().interrupt();
        }
        
        finally {
            log("Server closed successfully");
        }
    }
    
    public static void main(String[] args)
    {
        String hostname;
        int port;
        
        switch (args.length) {
            case 1:
                hostname = "localhost";
                port = Integer.valueOf(args[0]);
                break;
            
            case 2:
                hostname = args[0];
                port = Integer.valueOf(args[1]);
                break;
            
            default:
                System.out.println("Usage: java com.routingengine.server.Server [hostname] port");
                return;
        }
        
        try (Server server = new Server(hostname, port)) {
            server.run();
        }
    }
}
