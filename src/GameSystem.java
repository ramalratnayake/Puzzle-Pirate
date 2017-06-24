
import material.MaterialButton;

import javax.swing.*;
import javax.swing.Box;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;



public class GameSystem extends JFrame {
	private int boxNumbers;
	//private boolean isTrue = true;
private	MusicThread thread;
private int gameMode;
private int difficulty;
private Visual visual;


	public GameSystem(int difficulty, ArrayBlockingQueue<MapRep> mapQueue, int gameMode) throws IOException {
		super("Super Duper Adventure");
		//numMoves();

		setLayout(new BorderLayout());

		Point p = getLocation();
		int x = 0;
		int y = 0;
		p.setLocation(x, y);
		//Point q = getLocation();

		this.difficulty = difficulty;

		//gThread.start();

		Visual visual = new Visual(this.difficulty,mapQueue, gameMode);
		this.visual = visual;


		Color main = new Color(111, 84, 128);
		MaterialButton button = new MaterialButton("Reset", main, new Color(255, 255, 232),  new Color(241, 121, 161));
		MaterialButton button2 = new MaterialButton("Undo", main, new Color(255, 255, 232),  new Color(241, 121, 161));
		MaterialButton button3 = new MaterialButton("Mute", main, new Color(255, 255, 232),  new Color(241, 121, 161));
		MaterialButton button4 = new MaterialButton("Main Menu", main, new Color(255, 255, 232),  new Color(241, 121, 161));
		MaterialButton button5 = new MaterialButton("AI Solve", main, new Color(255, 255, 232), new Color(241, 121, 161));



		Dimension button_size = getPreferredSize();
		button_size.setSize(100,40);
		button.setMaximumSize(button_size);
		button2.setMaximumSize(button_size);
		button3.setMaximumSize(button_size);
		button5.setMaximumSize(button_size);
		Font menuFont = new Font("/src/material/Roboto-Light.ttf",Font.PLAIN,11);
		button4.setFont(menuFont);
		button4.setMaximumSize(button_size);

		Container c = getContentPane();
		thread = new MusicThread("start.wav");
		c.add(visual);


		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.PAGE_AXIS));
		subPanel.setBounds(0, 0, 400, 400);
		subPanel.setBorder(BorderFactory.createEmptyBorder(20, 37, 10, 50));
		subPanel.add(button);
		subPanel.add(Box.createRigidArea(new Dimension(20,10)));
	    subPanel.add(button2);
		subPanel.add(Box.createRigidArea(new Dimension(20,10)));
		subPanel.add(button3);
		subPanel.add(Box.createRigidArea(new Dimension(20,10)));
		if(this.difficulty==1 || this.difficulty ==2){
			subPanel.add(button5);
			subPanel.add(Box.createRigidArea(new Dimension(20,10)));
		}
		subPanel.add(button4);
		subPanel.add(Box.createRigidArea(new Dimension(20,30)));


		subPanel.add(visual.label1());
		if(gameMode != 0) subPanel.add(visual.label2());
		if(gameMode == 2) {
			subPanel.add(visual.label3());
		}
		
		c.add(subPanel, BorderLayout.EAST);
	    subPanel.setBackground(new Color(90,219,255));
		visual.requestFocus();
		
		



		subPanel.add(visual.label1());
		if(gameMode == 1) subPanel.add(visual.label2());

		c.add(subPanel, BorderLayout.EAST);
	    subPanel.setBackground(new Color(150, 45, 62));
		visual.requestFocus();


		thread.run();
		//disabled bc i cant hate this song and if this plays every 10 seconds i will hate it


		button.addActionListener(e -> {
            System.out.println("Reset game!");
            visual.mapReset();
            visual.requestFocus();
        });
		button2.addActionListener(e -> {
            System.out.println("Move undone!");
            visual.mapUndo();
            visual.requestFocus();
        });

		button3.addActionListener(e -> {
			thread.mute();
            System.out.println("Muted");
        });
		button4.addActionListener(evt -> {

                Start game;
                JFrame window = new MainFrame();
                try {
                	
                    game = new Start();
                    window.add(game);
                    window.pack();
                    window.setLocationRelativeTo(null);
                    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    window.setResizable(false);
                    window.setVisible(true);
                    thread.close();
                    dispose();
                   	visual.disposeTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    dispose();
                }

        });
		button5.addActionListener(e -> {
			visual.mapSimulate();
        });



	}


	public void setBoxNumbers(int number){
		this.boxNumbers = number;
	}


	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public Visual getVisual(){
		return this.visual;
	}


}

