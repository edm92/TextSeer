package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
//import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Splash extends JDialog{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel main;
	private Point initialClick;
	private JLabel closeLabel;
	private JLabel screenImage;
	private JPanel closePanel;
	
	private ImageIcon closeNormal;
	private ImageIcon closeHover;
	private ImageIcon closePress;
	private ImageIcon screen;
	
	public Splash()
	{
		this( null, null );
	}
	
	public Splash( String title )
	{
		this( null, title );
	}
	
	public Splash( JFrame frame )
	{
		this( frame, null );
	}
	
	public Splash( JFrame frame, String title )
	{
		super( frame, title );
		setTitle( title );
		initialize();
		showWindow();
		installListeners();
	}
	
	private void initialize()
	{
		setUndecorated( true );
		contentPane = new JPanel();
		setContentPane( contentPane );
		
		
		if ( main == null )
		{
			main = new JPanel();
		}
		
		closePanel = new JPanel( new BorderLayout() );
		initialClick = new Point();
	}
	
	public JPanel getContentPane()
	{
		return main;
	}
	
	public void setLayout( LayoutManager manager )
	{
		if ( main == null  )
		{
			main = new JPanel();
			main.setLayout( new FlowLayout() );
		}
		else
		{
			main.setLayout( manager );
		}
		
		if ( !(getLayout() instanceof BorderLayout) )
		{
			super.setRootPaneCheckingEnabled( false );
			super.setLayout( new BorderLayout() );
			super.setRootPane( super.getRootPane() );
			super.setRootPaneCheckingEnabled( true );
		}
	}

	/**
	 * Sets the background color of the Frame and all panels
	 * 
	 */
	public void setBackground( Color color )
	{
		super.setBackground( color );
		
		if ( contentPane != null )
		{
			contentPane.setBackground( color );
			closePanel.setBackground( color );
			main.setBackground( color );
		}
	}
	
	/**
	 * Add a Component to the main content Panel
	 */
	public Component add( Component comp )
	{
		return main.add( comp );
	}
	
	private void showWindow()
	{
		// If not set, default to FlowLayout
		if ( main.getLayout() == null )
		{
			setLayout( new FlowLayout() );
		}
		
		// close "button" - show this image by default
		closeNormal = 
			new ImageIcon( std.objects.splash_close ) ;
		// close "button" - show this when the mouse enter is detected
		closeHover = 
			new ImageIcon( std.objects.splash_hover );
		// close "button" - show this image when mouse press is detcted
		closePress = 
			new ImageIcon( std.objects.splash_press );
		
		screen = 
			new ImageIcon( std.objects.splash );
		screenImage = new JLabel( screen );
		closeLabel = new JLabel( closeNormal );
		//closeLabel.setOpaque(true);
		// Put the label with the image on the far right
		//closePanel.add( closeLabel, BorderLayout.EAST );
		
		// Add the two panels to the content pane
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( closePanel, BorderLayout.NORTH );
		contentPane.add( main, BorderLayout.CENTER );
		
		main.add(screenImage);
		
		// set raised beveled border for window
		contentPane.setBorder( 
				BorderFactory.createRaisedBevelBorder() );

		// Set position somewhere near the middle of the screen
		Dimension screenSize = 
			Toolkit.getDefaultToolkit().getScreenSize();
		setLocation( screenSize.width / 2 - ( getWidth()/ 2 ),
                   screenSize.height / 2 - ( getHeight() / 2 ) );
		
		// keep window on top of others
		setAlwaysOnTop( true );
	}
	
	/* 
	 * Add all listeners
	 */
	private void installListeners()
	{
		// Get point of initial mouse click
		addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				initialClick = e.getPoint();
				
				getComponentAt( initialClick );
			}
		});
		
		// Move window when mouse is dragged
		addMouseMotionListener( new MouseMotionAdapter()
		{
			public void mouseDragged( MouseEvent e )
			{
				// get location of Window
				int thisX = getLocation().x;
				int thisY = getLocation().y;
				
				// Determine how much the mouse moved since the initial click
				int xMoved = 
					( thisX + e.getX() ) - ( thisX + initialClick.x );
				int yMoved = 
					( thisY + e.getY() ) - ( thisY + initialClick.y );
				
				// Move window to this position
				int X = thisX + xMoved;
				int Y = thisY + yMoved;
				setLocation( X, Y );
			}
		});
		
		// Close "button" (image) listeners
		closeLabel.addMouseListener( new MouseAdapter()
		{
			public void mousePressed( MouseEvent e )
			{
				closeLabel.setIcon( closePress );
			}
			
			public void mouseReleased( MouseEvent e )
			{
				closeLabel.setIcon( closeNormal );
			}
			
			public void mouseEntered( MouseEvent e )
			{
				closeLabel.setIcon( closeHover );
			}
			
			public void mouseExited( MouseEvent e )
			{
				closeLabel.setIcon( closeNormal );
			}
			
			public void mouseClicked( MouseEvent e )
			{
				
				close();
			}
		});
	}

	// close and dispose
	public void close()
	{
		setVisible( false );
		dispose();
	}
	
	// Test
//	public static void main( String args[] )
//	{
//		Runnable runner = new Runnable()
//		{
//			public void run()
//			{
//				Splash window = new Splash();
//				window.setSize( new Dimension( 300, 200 ) );
//				window.setVisible( true );
//			}
//		};
//		EventQueue.invokeLater( runner );
//	}
}
