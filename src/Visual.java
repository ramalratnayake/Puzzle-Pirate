
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.Timer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class Visual extends Canvas implements ActionListener{
    
    private Image dbImage; //double buffer image
    private Graphics dbG; //double buffer graphics
    private Graphics player;
    private BufferedImage wall;
    private BufferedImage box;
    private BufferedImage character;
    private BufferedImage water;
    private BufferedImage down;
    private BufferedImage up;
    private BufferedImage right;
    private BufferedImage left;
    private BufferedImage goalempty;
    private BufferedImage goalfull;
    private BufferedImage spriteSheet;
    private Timer timer = new Timer(300, this);
    private Timer timer2= new Timer(1, this);
    private int timeLeft;
    private List<Integer> moves;
    private Iterator<Integer> soln;
    private SimpleDateFormat df=new SimpleDateFormat("mm:ss");
    

    private int difficulty; 
    private int gameMode; 
    private int score; 
    private int record;

    MapRep map;
    int windowDimension;
    JLabel label1;
    JLabel label2;
    JLabel label3;
    JLabel label4;
    int numMoves = 0;
    boolean isTimeLeft = true;
    ImageIcon newIcon; 
    private ArrayBlockingQueue < MapRep > mapQueue;
    
    public Visual(int difficulty, ArrayBlockingQueue <MapRep> maps, int gameMode) throws IOException {


        numMoves = 0;
        this.wall = ImageIO.read(new File("wall.png"));
        this.box = ImageIO.read(new File("box.png"));
        BufferedImage spriteSheet = ImageIO.read(new File("spritesheet.png"));
        this.down = spriteSheet.getSubimage(0, 0, 50, 66);
        this.up = spriteSheet.getSubimage(93, 0, 50, 66);
        this.right = spriteSheet.getSubimage(50, 0, 45, 66);
        this.left = spriteSheet.getSubimage(141, 0, 44, 66);
        this.character = down;
        this.water = ImageIO.read(new File("water.png"));
        this.goalempty = ImageIO.read(new File("goal.png"));
        this.goalfull = ImageIO.read(new File("fullgoal.png"));
        this.windowDimension = 700; //can change height of box
        
        this.difficulty = this.difficulty;
        this.gameMode = gameMode;

        newIcon = new ImageIcon("spriteStatic.png");

        setSize(new Dimension(windowDimension, windowDimension));
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                moveIt(evt);
            }
        });
        
        this.mapQueue = maps;
        // initial map
        
        MapRep m = null;
        while(m == null) {
            System.out.println("Loading...");
            try{
                m = this.mapQueue.poll( 3 , TimeUnit.SECONDS );
            } catch (InterruptedException e) {
                m = null;
            }
        }
        this.map = m;
        
        
        
        this.requestFocus(); //enter level (constant) to denote number of undos
        
        timeLeft = this.setTime(difficulty);
        System.out.println(timeLeft);
       
        if(gameMode != 0) {
            timer2.start();
            label2 = new JLabel ("Time Left: " + df.format(timeLeft));
        }
        
        label1 = new JLabel("Moves: " + getMoves(), null, JLabel.RIGHT);
        label3 = new JLabel("Score: " + score, null, JLabel.RIGHT);
    }
    
    public void paint(Graphics g) { //implementing double buffers
        update(g);
    }
    
    @Override
    public void update(Graphics g) {
        Image dbImage = createImage(getWidth(), getHeight());
        Graphics dbG = dbImage.getGraphics();
        Graphics player = dbImage.getGraphics();
         paintComponent(dbG, player);
         g.drawImage(dbImage, 0, 0, this);
         label1();
         label3();
    }
   
    public void paintComponent(Graphics g, Graphics player) {


        g.drawImage(water, 0, 0, null);

        int sizePerNode = windowDimension / map.getHeight();


        if(!isTimeLeft){
            return;
        }
        if (map.isGameFinished()) {
            nextLevel();
        System.out.println("finished!");
            score++;
            
            return;
        }
        
        Iterator <EndLoc> it = map.getAllEndLocs();
        EndLoc e;
        
        while (it.hasNext()) {
            e = it.next();
//            g.setColor(Color.GREEN);
//            g.fillRect(e.getPosX() * sizePerNode, e.getPosY() * sizePerNode, sizePerNode, sizePerNode);
                 g.drawImage(goalempty, e.getPosX()* sizePerNode, e.getPosY() * sizePerNode, null);

        }
        
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                
                switch (map.whatsInside(i, j)) {
                    case "Wall":
                        g.drawImage(wall, i * sizePerNode, j * sizePerNode, null);
                        break;
                    case "Box":
                        g.drawImage(box, i * sizePerNode, j * sizePerNode, null);
                        break;
                    case "BoxInLoc":
                        g.drawImage(goalfull, i * sizePerNode, j * sizePerNode, null);

                        break;
                    case "Character":
                        player.drawImage(character, i * sizePerNode, j * sizePerNode, null);
                        break;
                }
                
            }
        }
    }
    
    public void moveIt(KeyEvent evt) {
        
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                map.moveRequest(MapRep.DOWN);
                updateDirection(MapRep.DOWN);
                break;
            case KeyEvent.VK_UP:
                map.moveRequest(MapRep.UP);
                updateDirection(MapRep.UP);
                break;
            case KeyEvent.VK_LEFT:
                map.moveRequest(MapRep.LEFT);
                updateDirection(MapRep.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                map.moveRequest(MapRep.RIGHT);
                updateDirection(MapRep.RIGHT);
                break;
            case KeyEvent.VK_R:
                map.reset();
                break;
            case KeyEvent.VK_Z:
                map.undo();
                break;
            case KeyEvent.VK_H:
                Solver s = new Solver(map);
                moves = s.getSolution();
                if(moves != null){
                    soln = moves.iterator();
                    timer.start(); //loads the solution and starts timer for painting
                }
                else{
                    JOptionPane.showMessageDialog(null,"No solution, please reset map","Solver failed :(",JOptionPane.INFORMATION_MESSAGE,newIcon);
                }
                return;
        }
        repaint();
    }
    
    
    /**
     * @post applies next move of solution and repaints every 300ms
     */
    public void actionPerformed(ActionEvent ev){
        if(ev.getSource()==timer){
            if(soln.hasNext()) {
                int move = soln.next();
                map.moveRequest(move);
                updateDirection(move);
                repaint();
            }
        }
  
        else if(ev.getSource()==timer2){
            countDown();

        }
    }
    
    
    /**
    *
    * Helper function to reset the map
    *
    * @post returns map to initial generated state, see MapGenerator for more details
    *
    */
    public void mapReset() {
        map.reset();
        repaint();
    }
    
    public void mapSimulate(){
        Solver s = new Solver(map);
        moves = s.getSolution();
        if(moves != null){
            soln = moves.iterator();
            timer.start(); //loads the solution and starts timer for painting
        }
        return;
    }
    
    /**
    *
    * Helper function to undo move
    *
    * @post returns map with last move undone
    *
    */
    public void mapUndo() {
        map.undo();
        repaint();
    }





   public void nextLevel() {

        MapRep m = null;
        while(m == null) {
            System.out.println("Loading...");
            try{
                m = this.mapQueue.poll( 3 , TimeUnit.SECONDS );
            } catch (InterruptedException e) {
                m = null;
            }
        }
        this.map = m;

       repaint();
   }



   public int getMoves(){

        numMoves = map.numMoves();
        return this.numMoves;
    }
   
    
    public JLabel label1() {
        label1.setForeground(Color.WHITE);
        this.label1.setText("Moves: " + getMoves());
        return this.label1;
    }
    
    public JLabel label2() {
        label2.setForeground(Color.WHITE);
        this.label2.setText("Time Left: " + df.format(timeLeft));
        return this.label2;

    }

    public JLabel label3(){
        label3.setForeground(Color.WHITE);
        this.label3.setText("Score: " + score);
        return this.label3;
    }

    
    private void updateDirection(int direction){
        switch(direction){
            case MapRep.UP: 
                character = up; 
                break;
            case MapRep.DOWN:
                character = down;
                break;
            case MapRep.LEFT:
                character = left;
                break;
            case MapRep.RIGHT:
                character = right;
                break;  
        }
    }
    
    
    private boolean countDown(){

        if(this.gameMode == 0){
            timeLeft = -1;

        }

        if(timeLeft == 0 && gameMode == 1){
            isTimeLeft = false;
            Object[] options = {"EXIT","Play Again",};
            int n = JOptionPane.showOptionDialog(null, "You ran out of time :(","TIMEOUT",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,  newIcon, options, options[0]);
            if(n == JOptionPane.YES_OPTION){
                JOptionPane.getRootFrame().dispose();
                timeLeft = -1;

            }
            else{
                nextLevel();

            }


            repaint();
            return isTimeLeft; 
        }

        if(timeLeft == 0){
            String message;
            isTimeLeft = false;

            record = score; 
            message = "Congratulations, you completed " + score + " levels!";


            JOptionPane.showMessageDialog(null, message,"Timeout",JOptionPane.INFORMATION_MESSAGE,newIcon);
            timeLeft = -1;
            repaint();
            return isTimeLeft;
        }
        
        if(timeLeft > 0){       
            timeLeft--;
            label2();
            isTimeLeft = true;
        }

        return isTimeLeft;
        
    }
    
    /**
     * @param difficulty
     * @pre the correct constant for difficulty has been passed in
     * @return the allowed time per difficulty
     */
    private int setTime(int difficulty){
        if(gameMode == 1){
            switch(difficulty){

                case 0 : return 15000;
                case 1 : return 30000;
                case 2 : return 45000;

            }
        }
        else if(gameMode == 2){
            return 120000;
        }
        return 0;
    }


    public void disposeTimer(){
        this.gameMode = 0;
        countDown();
    }
}