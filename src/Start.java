import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Start extends JComponent {
	BufferedImage intro;

	public Start() throws IOException {
		intro = ImageIO.read(new File("Intro.png"));


	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(800,400);
			
	}
	
	@Override
	protected void paintComponent(Graphics g){
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, 800, 600);
		
		g.drawImage(intro, 0 ,0, null);

	}


}
