
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MapRep implements Comparable<MapRep> {
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int NOWHERE = 5;
	

	private Node[][] state;
	private int mapHeight;
	private int mapWidth;
	private int level;
	private boolean isSim;
	private Set<EndLoc> allEndLocs;
	private List<Box> allBoxes;
	private MapRepHeuristic heuristic;
	private int heuristicVal;
	private int lastMove;
	private MapRep initial;
	private Deque<MapRep> prevMoves;
	private Character player;
	private TempBox tempBox;
	private int[] pushCoord;
	private int moves = 0; 

	/**
	 * A instantiates a MapRep class and initializes accordingly
	 * @param height of required map
	 * @param width of required map
	 * @param level that has been selected
	 * @param whether this constructor is being called during a clone
	 */
	public MapRep(int height, int width, int level, boolean isClone){
		moves = 0;
		this.allEndLocs = new HashSet<EndLoc>();
		this.allBoxes = new ArrayList<Box>();
		this.mapWidth = width;
		this.mapHeight = height;
		this.level = level;
		prevMoves = new ArrayDeque<MapRep>();

		this.state = new Node[width][height];

		/*

		note: not sure how to best initialise node
		especially for clone state,

		*/

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				state[i][j] = new Node ();
			}
		}


		if(!isClone){ //only call map generator if isn't a clone
			MapGenerator g;
			do{
				
				g = new MapGenerator(this,this.level);
			
			}
			while(g.generateMap()!= true);
			// keep trying to generate until the generator is successful

			System.out.println("Seed of Map Generated: " + g.getSeed() );
		}

		initializeNodes();
		
		isSim = false;
		
		if(!isClone) initial = this.cloneState(); //only save initial if it isn't clone
	}
	
	/**
	 * @return a list collection of all the goal locations added
	 */
	public Iterator<EndLoc> getAllEndLocs(){
		return this.allEndLocs.iterator();
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @return string containing name of item in node
	 */
	public String whatsInside(int i, int j){
		if(getNode(i, j) == null) return "Nothing";
		String s = getNode(i, j).whatsInside();
		
		if(s.equals("Box")){
			if(this.allEndLocs.contains(new Box(i,j))) return "BoxInLoc";
		}
		
		return s;
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @param whether the player is allowed to push boxes (used for simulating)
	 * @post a character will be instantiated and placed in correct location
	 */
	public void spawnCharacter(int x, int y, boolean canPush){
		this.player = new Character(x,y);
		if(!canPush) player.setNoPush();
		this.addItemToLoc(player);
		
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post a box will be instantiated and added to correct location
	 */
	public void addBox (int x, int y){
		Box b = new Box(x,y);
		allBoxes.add(b);
		addItemToLoc(b);
	}

	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post a ground (just null) will be added to correct location
	 */
	public void addGround (int x, int y){
		state[x][y].moveItemIn(null,NOWHERE);
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post a temporary box (for map generation) will be instantiated and added to correct location
	 */
	public void addTempBox(int x, int y){
		TempBox b = new TempBox(x,y);
		this.tempBox = b;
		addItemToLoc(b);
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post a wall will be instantiated and added to correct location
	 */
	public void addWall(int x, int y){
		Wall w = new Wall(x,y);
		addItemToLoc(w);
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post a goal/end location will be instantiated and added to correct location
	 */
	public void addEndLoc(int x, int y){
		EndLoc e = new EndLoc(x,y);
		this.allEndLocs.add(e);
		addItemToLoc(e);
	}
	
	/**
	 * @param constant denoting desired move direction
	 * @post if move is legal the character is moved and all affected items will be updated
	 * @return if move was successful
	 */
	public boolean moveRequest(int moveDirection){
		Node oldNode = getNode(player.getPosX(), player.getPosY());
		Character tempChar = this.player.cloneWithNewLoc(moveDirection);
		Node newNode = getNode(tempChar.getPosX(), tempChar.getPosY());
		//boolean isSim = godMode;
		
		if(newNode.tryMoveIn(moveDirection, player, isSim)){ //if the newNode can be moved into
			this.lastMove = moveDirection;
			this.storePrevMove();
			newNode.moveItemIn(oldNode.moveItemOut(), moveDirection);//move item (the player) out of old node into new node
			this.player.moveInDirection(moveDirection);
			resetEndLocs();
			moves++;
			return true;
		}
	return false;
	}
	
	/**
	 * @return if the game has been completed (all end locations have been filled)
	 */
	public boolean isGameFinished(){
		Iterator <EndLoc> it = this.allEndLocs.iterator();
		Node n;
		EndLoc e;
		int i = 0;
		while(it.hasNext()){
			e = it.next();
			n = getNode(e.getPosX(), e.getPosY());
			if(n.whatsInside() != "Box") i++;
		}
		return (i == 0);
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post the coordinate of player when a push occurred (only the first will be stored)
	 */
	public void setPushCoord(int x, int y){
		if(pushCoord == null){
			this.pushCoord = new int[2];
			pushCoord[0] = x;
			pushCoord[1] = y;
		}	
	}
	
	/**
	 * @pre the map has been correctly instantiated (hence an initial state has been stored)
	 * @post reverts all items to initial position and resets all appropriate fields
	 */
	public void reset(){
		MapRep toCopy = initial.cloneState();
		this.allEndLocs = toCopy.allEndLocs;
		this.player = toCopy.player;
		this.state = toCopy.state;
		this.prevMoves.clear();
		this.moves = 0;
	}
	
	/**
	 * @pre map has been correctly instantiated (stack has been set up)
	 * @post all items will be moved to previous location 
	 */
	public void undo(){
		if(this.prevMoves.isEmpty())return;
		MapRep prev = this.prevMoves.pop();
		this.allEndLocs = prev.allEndLocs;
		this.player = prev.player;
		this.state = prev.state;
	}

	/**
	 * @param heuristic function
	 * @pre heuristic has been instantiated correctly
	 * @post heuristic will be loaded onto map and the heuristic value will be saved
	 */
	public void loadHeuristic(MapRepHeuristic h){
		this.heuristic = h;
		this.heuristicVal = h.heuristicVal();
	}
	

	
	
	/**
	 * @post the Maprep functions will be changed slightly to suit the solvers
	 */
	public void setIsSim(){
		this.isSim = true;
	}
	
	/**
	 * @return the number of moves made
	 */
	public int numMoves(){
		return this.moves; 
	}
	
	/**
	 * @post removes any end locations that have been placed (for map generation)
	 */
	public void clearEndLocs(){
		this.allEndLocs.clear();
	}

	
	/**
	 * @post removes the temporary box added during map generation
	 */
	public void removeTempBox(){
		getNode(tempBox.getPosX(),tempBox.getPosY()).moveItemOut();
		tempBox = null;
	}
	
	/**
	 * @post all saved moves will be removed
	 */
	public void clearMoves(){
		this.prevMoves.clear();
	}
	
	/**
	 * @post removes any players that have been added to the map (for map generation)
	 */
	public void removePlayers(){
		for(int j = 0; j < this.mapHeight ; j++){
			for(int i = 0; i < this.mapWidth; i++){
				if (this.state[i][j].whatsInside().equals("Character")){
					this.state[i][j].moveItemOut();
					this.player = null;
				}
			}
		}
	}
	
	/**
	 * @return deep clone of the map representation
	 */
	public MapRep cloneState(){
		MapRep cloned = new MapRep(this.mapHeight, this.mapWidth, this.level, true);
		
		cloned.prevMoves.addAll(this.prevMoves);
		
		for(int j = 0; j < this.mapHeight ; j++){
			for(int i = 0; i < this.mapWidth; i++){
				switch (this.state[i][j].whatsInside()){
					case "Box":
						cloned.addBox(i, j);	
						break;
					case "Wall":
						cloned.addWall(i, j);
						break;
					case "TempBox":
						cloned.addTempBox(i, j);
						break;
					case "Nothing":
						break;
				}
			}
		}
		
		cloned.allEndLocs.addAll(this.allEndLocs);
		cloned.pushCoord = this.pushCoord;
		cloned.player = this.player.clone();
		cloned.addItemToLoc(cloned.player);
		cloned.isSim = this.isSim;
		cloned.lastMove = this.lastMove;
		
		return cloned;
	}
	
	/**
	 * @pre appropriate heuristic loaded before this method call
	 * @return the heuristic value
	 */
	public int getHeuristic(){
		return this.heuristicVal;
	}
	
	/**
	 * @return last registered move
	 */
	public int getLastMove(){
		return this.lastMove;
	}
	
	/**
	 * @return coordinate of player when the first ever push for that state occurred (for map generation)
	 */
	public int[] getFirstPushCoord(){
		return pushCoord;
	}
	
	/**
	 * @return height (in number of nodes) of the map
	 */
	public int getHeight(){
		return this.mapHeight;
	}
	
	/**
	 * @return height (in number of nodes) of the map
	 */
	public int getWidth(){
		return this.mapWidth;
	}

	/**
	 * @return the character in the game
	 */
	public int[] getPlayerCoord(){
		return Arrays.copyOf(this.player.getLoc(),2);
	}
	
	/**
	 * @post prevents the player of this state from pushing any boxes
	 */
	public void playerNoPush(){
		this.player.setNoPush();
	}
	
	/**
	 * @return the coordinates of temporary box in the game
	 */
	public int[] getTempBoxCoords(){
		return Arrays.copyOf(this.tempBox.getLoc(),2);
	}
	
	public Iterator<MapRep> prevMovesIterator() {
		return this.prevMoves.descendingIterator();
	}
	
	public void getHelp(){
		(new Solver(this)).getSolution();
	}


	@Override
	public int hashCode(){
		if(tempBox != null){
			int[] coords = {this.player.getPosX(), this.player.getPosY()};
			return Arrays.hashCode(coords);
		}
		else{
			List<int[]> itemCoord = new ArrayList<int[]>();
			int result = 17;
			for(int i = 0; i < allBoxes.size(); i++){
				result = 37 * result + Arrays.hashCode(allBoxes.get(i).getLoc());
			}
			
			result = 37 * result + Arrays.hashCode(this.getPlayerCoord());
			
			return result;
		}
		
	}
	
	@Override
	public boolean equals(Object o){
		
		MapRep map = (MapRep) o;
		if(tempBox != null){
			if(map.player.getPosX() != this.player.getPosX()) return false;
			return map.player.getPosY() == this.player.getPosY();
		}
		else{
			//if (map.getHeuristic() == this.getHeuristic()) return true;
			
			for(int j = 0; j < this.mapHeight ; j++){
				for(int i = 0; i < this.mapWidth; i++){
					if(this.whatsInside(i, j).equals("Box")){
						if(!map.whatsInside(i, j).equals("Box")) return false;
					}
					if(this.whatsInside(i, j).equals("BoxInLoc")){
						if(!map.whatsInside(i, j).equals("BoxInLoc")) return false;
					}
				}
			}
			return this.player.equals(map.player);
		}
	}
	
	@Override
	public int compareTo(MapRep m){
		int compare = this.heuristic.heuristicVal() - m.heuristic.heuristicVal();
		if(compare > 0) return 1;
		if(compare < 0) return -1;
		return 0;
	}

	/**
	 * @post instantiating nodes at all locations of the map
	 */
	private void initializeNodes(){
		Node up;
		Node down;
		Node left;
		Node right;
		//setting the adjacent nodes for each node
		for(int j = 0; j < this.mapHeight ; j++){
			for(int i = 0; i < this.mapWidth; i++){
				left = (i == 0) ? null : this.state[i-1][j]; //if the node is at the end of graph then an adjacent node will be null
				right = (i == this.mapHeight-1)  ? null : this.state[i+1][j];
				
				up = (j == 0) ? null : this.state[i][j-1];
				down = (j == this.mapWidth-1)  ? null : this.state[i][j+1];
				
				state[i][j].setAdjacent(up, down, left, right);
			}
		}
	}
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @return if not out of bounds, the node at that coordinate
	 */
	private Node getNode(int x, int y){
		try{
			return this.state[x][y];
		}
		catch (IndexOutOfBoundsException e){
			return null;
		}
	}

	/**
	 * @param object that needs to be added
	 * @post item will be added to node (location extracted from object)
	 */
	private void addItemToLoc(Item i){
		state[i.getPosX()][i.getPosY()].moveItemIn(i, MapRep.NOWHERE);
	}
	
	/**
	 * @post end locations placed back into assigned nodes if player/box moves out of that location
	 */
	private void resetEndLocs(){
		Iterator <EndLoc> it = this.allEndLocs.iterator();
		Node n;
		EndLoc e;
		while(it.hasNext()){
			e = it.next();
			n = getNode(e.getPosX(), e.getPosY());
			if(n.whatsInside().equals("Nothing")){
				addItemToLoc(e);
			}
		}
	}
	
	/**
	 * @post the current state will be stored in a stack (bounded depending on level)
	 */
	private void storePrevMove(){
		MapRep m = this.cloneState();
		this.prevMoves.push(m);
		
		if(this.prevMoves.size() > numUndosPerLevel()){
			this.prevMoves.removeLast();
		}
	}
	
	/**
	 * @return number of undoes allowed depending on difficulty setting
	 */
	private int numUndosPerLevel(){
		if(isSim) return Integer.MAX_VALUE; //allow store all moves during AI solving
		
		switch (this.level){
			case Generator.EASY:
				return 20;
			case Generator.MEDIUM:
				return 10;
			case Generator.HARD:
				return 5;
		}
		return 0;
	}



}

