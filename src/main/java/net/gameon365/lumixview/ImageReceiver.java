package net.gameon365.lumixview;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter( AccessLevel.PRIVATE )
@Setter( AccessLevel.PRIVATE )
class ImageReceiver
{
    private final ImageDisplay display;
    
    private final InetAddress bindAddress;
    
    @Getter
    private final int port;
    
    private DatagramSocket socket;
    
    ImageReceiver( ImageDisplay display, InetAddress bindInterface, int port )
    {
        this.display = display;
        this.bindAddress = bindInterface;
        this.port = port;
    }
    
    ImageReceiver( ImageDisplay display )
    {
        InetAddress bindInterface = null;
        try
        {
            bindInterface = InetAddress.getLocalHost();
        }
        catch( UnknownHostException ex )
        {
            Logger.getLogger( ImageReceiver.class.getName() ).log( Level.SEVERE, "Couldn't get local address", ex );
            System.exit( 1 );
        }
        
        this.display = display;
        this.bindAddress = bindInterface;
        this.port = ThreadLocalRandom.current().nextInt( 49152, 65535 );
    }
    
    public void startServer()
    {
        try
        {
            this.setSocket( new DatagramSocket( this.getPort() ) );
            Logger.getLogger( ImageReceiver.class.getName() ).log( Level.INFO, "Server started on port {0}", this.getPort() );
        }
        catch( SocketException ex )
        {
            Logger.getLogger( ImageReceiver.class.getName() ).log( Level.SEVERE, "Couldn't open socket", ex );
        }
    }
    
    public void serverLoop()
    {
        DatagramPacket packet;
        byte[] data = new byte[30000];
        int offset = 132;
        
        while( true )
        {
            try
            {
                packet = new DatagramPacket( data, data.length, this.getBindAddress(), this.getPort() );
                this.getSocket().receive( packet );
                
                for( int i = 130; i < 320; i += 1 )
                {
                    if( data[i] == -1 && data[i+1] == -40 )
                    {
                         offset = i;
                    }
                }
                
                this.getDisplay().updateImage( ImageIO.read( new ByteArrayInputStream( Arrays.copyOfRange( data, offset, packet.getLength() ) ) ) );
            }
            catch( IOException ex )
            {
                Logger.getLogger( ImageReceiver.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }
    }
}
