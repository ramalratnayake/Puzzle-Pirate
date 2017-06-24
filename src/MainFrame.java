
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.border.EmptyBorder;
import material.MaterialButton;

import java.util.concurrent.ArrayBlockingQueue;


public class MainFrame extends JFrame{
	MusicThread thread;
	
	GeneratorThread genThread;

	private int gameMode; //0 endless, 1 time trail, 2, comp
		//endless: generates maps immediately after a player completes it, no timelimit
		//timetrail: same as endless, as many as you can finish in 3 minutes
		//comp: generates the same map and players compete to get the shortest time

	private ArrayBlockingQueue<MapRep> mapQueue;

	
	public MainFrame(){
		super("Super Duper Adventure");

		//Set layout manager 
		setLayout(new BorderLayout());


		//Create Swing Component 
		//JButton button = new JButton("Play now");
		Color matOrange = new Color(255, 127, 102);
		Color matWhite = new Color(255, 255, 232);
		Color matPink = new Color(241, 121, 161);

		MaterialButton button = new MaterialButton("Play now", matOrange, matWhite,  matPink);
		MaterialButton easy = new MaterialButton("Easy", matOrange, matWhite,  matPink);
		MaterialButton medium = new MaterialButton("Medium", matOrange, matWhite,  matPink);
		MaterialButton hard = new MaterialButton("Hard", matOrange, matWhite,  matPink);
		MaterialButton endless = new MaterialButton("Endless", matOrange, matWhite,  matPink);
		MaterialButton timeTrial = new MaterialButton("Time Trial", matOrange, matWhite,  matPink);
		MaterialButton challenge = new MaterialButton("Challenge", matOrange, matWhite,  matPink);
		JLabel test = new JLabel("test");


		Dimension button_size = getPreferredSize();
		button_size.setSize(140,50);
		button.setPreferredSize(button_size);
		easy.setPreferredSize(button_size);
		medium.setPreferredSize(button_size);
		hard.setPreferredSize(button_size);
		endless.setPreferredSize(button_size);
		timeTrial.setPreferredSize(button_size);
		challenge.setPreferredSize(button_size);

		Font myFont = new Font("/Roboto-Thin.ttf", Font.PLAIN, 25);
		Font smallFont = new Font("/Roboto-Thin.ttf", Font.PLAIN, 20);


		button.setFont(myFont);
		easy.setFont(myFont);
		medium.setFont(myFont);
		hard.setFont(myFont);
		endless.setFont(myFont);
		timeTrial.setFont(smallFont);
		smallFont = new Font("/Roboto-Thin.ttf", Font.PLAIN, 16);

		challenge.setFont(smallFont);

		//Add Swing Components to content panel
		Container c = getContentPane();

		thread = new MusicThread("start.wav");
	    thread.run();



		JPanel subPanel = new JPanel();
		c.add(subPanel, BorderLayout.SOUTH);
		subPanel.add(button);
		subPanel.add(easy);
		subPanel.add(medium);
		subPanel.add(hard);
		subPanel.add(endless);
		subPanel.add(timeTrial);
		subPanel.add(challenge);
		subPanel.setBorder(new EmptyBorder(20,0,20,0));
		subPanel.requestFocus();

		easy.setVisible(false);
		medium.setVisible(false);
		hard.setVisible(false);
		endless.setVisible(false);
		timeTrial.setVisible(false);
		challenge.setVisible(false);


		subPanel.setBackground(new Color(2, 136, 209));


		this.mapQueue = new ArrayBlockingQueue<MapRep>(3);


		//Behaviour
		button.addActionListener(e -> {
            button.setVisible(false);
            endless.setVisible(true);
            timeTrial.setVisible(true);
            challenge.setVisible(true);

        });
		endless.addActionListener(e -> {
            System.out.println("Endless game level selected");
            gameMode = 0;
            endless.setVisible(false);
            timeTrial.setVisible(false);
            challenge.setVisible(false);
            easy.setVisible(true);
            medium.setVisible(true);
            hard.setVisible(true);
        });
		timeTrial.addActionListener(e -> {
            System.out.println("Time Trail selected");
            gameMode = 1;
            endless.setVisible(false);
            timeTrial.setVisible(false);
            challenge.setVisible(false);
            easy.setVisible(true);
            medium.setVisible(true);
            hard.setVisible(true);
        });
		challenge.addActionListener(e -> {
            System.out.println("Challenge selected");
            gameMode = 2;
            endless.setVisible(false);
            timeTrial.setVisible(false);
            challenge.setVisible(false);
            easy.setVisible(true);
            medium.setVisible(true);
            hard.setVisible(true);
        });

		easy.addActionListener(e -> {
            System.out.println("Easy game selected");
            	GameSystem frame;

                try {


                    genThread = new GeneratorThread(Generator.EASY, mapQueue);
                    genThread.start();

                    frame = new GameSystem(Generator.EASY, mapQueue, gameMode);
                    frame.setSize(900, 720);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                    frame.setResizable(false); //set this to false later so that player cannot view free space
                    frame.setBoxNumbers(1);
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.requestFocus();
                    frame.pack();
                    frame.setDifficulty(1); //0 meaning easy
                    frame.getVisual().requestFocus();
                    dispose();
                    thread.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            });
		medium.addActionListener(e -> {
            System.out.println("Medium game selected");
            GameSystem frame;
            
            try {

                genThread = new GeneratorThread(Generator.MEDIUM,mapQueue);
                genThread.start();
                
                frame = new GameSystem(Generator.MEDIUM,mapQueue, gameMode);
                frame.setSize(900,720);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setResizable(false); //set this to false later so that player cannot view free space
                frame.setBoxNumbers(2);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.requestFocus();
                frame.pack();
                frame.setDifficulty(2); //1 meaning medium
                frame.getVisual().requestFocus();

                dispose();
                thread.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
		hard.addActionListener(e -> {
            System.out.println("Hard game selected");

            GameSystem frame;

            try {
				dispose();


                genThread = new GeneratorThread(Generator.HARD,mapQueue);
                genThread.start();

                frame = new GameSystem(Generator.HARD,mapQueue, gameMode);
                frame.setSize(900,720);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setResizable(false); //set this to false later so that player cannot view free space
                frame.setBoxNumbers(3);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.requestFocus();
                frame.pack();


                frame.setDifficulty(3); //2 meaning hard
                frame.getVisual().requestFocus();

                dispose();
                thread.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });


	}


}
