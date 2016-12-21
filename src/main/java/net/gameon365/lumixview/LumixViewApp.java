package net.gameon365.lumixview;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;

@Getter( AccessLevel.PRIVATE )
public class LumixViewApp
{
    private final ImageDisplay display;
    
    private final ImageReceiver server;
    
    private final RequestHandler handler;
    
    public static void main( String[] args )
    {
        if( args.length < 1 )
        {
            Logger.getLogger( LumixViewApp.class.getName() ).log( Level.SEVERE, "Error: No camera IP address provided." );
            System.exit( 1 );
        }
        
        InetAddress camera = null;
        try
        {
            camera = InetAddress.getByName( args[0] );
        }
        catch( UnknownHostException ex )
        {
            Logger.getLogger( LumixViewApp.class.getName() ).log( Level.SEVERE, "Error: Invalid camera IP/host." );
            System.exit( 1 );
        }
        new LumixViewApp( camera ).run();
    }
    
    public LumixViewApp( InetAddress camera, ImageDisplay display )
    {
        this.display = display;
        this.server = new ImageReceiver( this.getDisplay() );
        this.getServer().startServer();
        this.handler = new RequestHandler( camera, this.getServer().getPort() );
    }
    
    public LumixViewApp( InetAddress camera )
    {
        this( camera, new SwingImageDisplay() );
    }
    
    public void run()
    {
        this.getServer().serverLoop();
    }
}
