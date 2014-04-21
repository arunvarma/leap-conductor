package frontend;

/**
 * GUI
 * a graphical user interface for the leap conductor
 * @author Alex Bok
 */

import java.awt.*;
import javax.swing.*;
import backend.audio.*;

public class GUI extends JFrame {
	
  private VisualizerPanel _visualizerPanel;
  private SongPanel _songPanel;
  public static final int WIDTH = 1300;
  public static final int HEIGHT = 700;
  
  public GUI(SongApp songApp) {
	    // set up frame
	    super("Leap Conductor");
	    this.setSize(new Dimension(WIDTH, HEIGHT));

        // play song
        SongApp.playSong();

	    // set up panels
	    _visualizerPanel = new VisualizerPanel(songApp, 5000, 3);
	    _songPanel = new SongPanel();
	    
	    // add components
	    this.add(_visualizerPanel, BorderLayout.CENTER);
	    this.add(_songPanel, BorderLayout.NORTH);
	    
	    this.setResizable(false);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setVisible(true);
  }
}
