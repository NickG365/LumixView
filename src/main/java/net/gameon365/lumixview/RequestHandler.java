package net.gameon365.lumixview;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;

@Getter( AccessLevel.PRIVATE )
public class RequestHandler implements Runnable
{
    public static final String INITIALIZE_URL = "http://%s/cam.cgi?mode=camcmd&value=recmode";
    
    public static final String START_URL = "http://%s/cam.cgi?mode=startstream&value=%d";
    
    public static final String KEEPALIVE_URL = "http://%s/cam.cgi?mode=getstate";
    
    public static final String STOP_URL = "http://%s/cam.cgi?mode=stopstream";
    
    private final InetAddress camera;
    
    private final int serverPort;
    
    private final ScheduledExecutorService scheduler;
    
    public RequestHandler( InetAddress camera, int serverPort )
    {
        this.camera = camera;
        this.serverPort = serverPort;
        this.scheduler = Executors.newScheduledThreadPool( 1 );
        
        this.makeInitialRequest();
        this.getScheduler().scheduleAtFixedRate( this, 10, 10, TimeUnit.SECONDS );
    }
    
    @Override
    public void run()
    {
        this.makeKeepAliveRequest();
    }
    
    public void shutdown()
    {
        this.makeStopRequest();
    }
    
    private void makeInitialRequest()
    {
        this.makeRequest( String.format( RequestHandler.INITIALIZE_URL, this.getCamera().getHostAddress() ) );
        this.makeRequest( String.format( RequestHandler.START_URL, this.getCamera().getHostAddress(), this.getServerPort() ) );
    }
    
    private void makeKeepAliveRequest()
    {
        this.makeRequest( String.format( RequestHandler.KEEPALIVE_URL, this.getCamera().getHostAddress() ) );
    }
    
    private void makeStopRequest()
    {
        this.makeRequest( String.format( RequestHandler.STOP_URL, this.getCamera().getHostAddress() ) );
    }
    
    private void makeRequest( String url )
    {
        Logger.getLogger( RequestHandler.class.getName() ).log( Level.INFO, "Requesting: {0}", url );
        
        try
        {
            Logger.getLogger( RequestHandler.class.getName() ).log( Level.INFO, new java.util.Scanner( new URL( url ).openStream() ).useDelimiter( "\\A" ).next() );
        }
        catch( MalformedURLException ex )
        {
            Logger.getLogger( ImageReceiver.class.getName() ).log( Level.SEVERE, "Malformed URL", ex );
        }
        catch( IOException ex )
        {
            Logger.getLogger( ImageReceiver.class.getName() ).log( Level.SEVERE, "Unable to connect to camera", ex );
        }
    }
}
