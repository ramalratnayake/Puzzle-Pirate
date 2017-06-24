
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.io.IOException;

public class Main {
	
	
	public static void main(String[]args) throws IOException {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Start game;
				//Box gameBox;
				JFrame window = new MainFrame();
				
				try {
					game = new Start();
					window.add(game);
					window.pack();
					window.setLocationRelativeTo(null);
					window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					//INCREASE THE SIZE when using maximise
					window.setResizable(false);
					window.setVisible(true);


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
