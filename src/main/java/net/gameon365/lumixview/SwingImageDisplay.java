package net.gameon365.lumixview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter( AccessLevel.PRIVATE )
@Setter( AccessLevel.PRIVATE )
public class SwingImageDisplay extends JPanel implements ImageDisplay
{
    private final JFrame frame;
    
    BufferedImage image;
    
    public SwingImageDisplay()
    {
        this.frame = new JFrame( "Lumix Live View" );
        
        this.getFrame().addComponentListener( new ComponentAdapter()
        {
            // Thanks http://stackoverflow.com/a/19865135
            @Override
            public void componentResized( ComponentEvent e )
            {
                Rectangle bounds = e.getComponent().getBounds();
                e.getComponent().setBounds( bounds.x, bounds.y, bounds.width, bounds.width * 3 / 4 );
            }
        } );
        
        this.getFrame().addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        } );
        
        this.getFrame().add( this );
        this.getFrame().pack();
        this.getFrame().setVisible( true );
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( 640, 480 );
    }
    
    @Override
    public void paint( Graphics g )
    {
        if( this.getImage() != null )
        {
            g.drawImage( this.getImage(), 0, 0, this.getWidth(), this.getHeight(), null );
        }
    }
    
    @Override
    public void updateImage( BufferedImage image )
    {
        this.setImage( image );
        this.repaint();
    }
}
