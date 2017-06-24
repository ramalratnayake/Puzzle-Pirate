import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.NullPointerException;

import java.util.*;
import java.util.concurrent.*;


/**
 * Map Generator implementation 
 *
 * 
 *
 *
 *
 *
 *
 * @author Oscar Downing (z5114817)
 */

public class MapGenerator extends Templator implements Generator  {


	private MapRep m;
	//private int width;
	//private int height;
	private int level;

	private int numBadLocs;

	private RandomGenerator rand;

	private List<int[]> prevBoxCords;
	private List<int[]> prevGoalCords;


	/** 
     * Map Generator constructor - instantiates the map generator class
     * 
     * 
     * @param m - reference to map rep, contains all map info and methods
     *		  level - the difficulty of the map generated  
     * @pre m != null, and level == 1 or level == 2 or level == 3 
     * @post Map Generator object is instantiated
     */
	public MapGenerator(MapRep m, int level) {

		super(m.getWidth(),m.getHeight());

		/* Map m info */
		this.m = m;

		this.level = level;

		this.numBadLocs = 0;

		this.rand = new RandomGenerator();
		//this.templator = new Templator(this.rand,this.width,this.height);

		this.prevBoxCords = new ArrayList< int[] > ();
		this.prevGoalCords = new ArrayList< int[] > ();
	}

	public long getSeed() {
		return this.rand.getSeed();
	}


	/* === Methods === */

	/** 
     * Generate method, involves 3 steps; building prototype, adding templates and then evaluation/placement
     * 
     * @pre correctly instantiated 
     * @post the 2d array in mapRep with be populated with walls, one character, boxes and goals.
     * @return true - successfully generated, false - not successful
     */
	@Override 
	public boolean generateMap() {

		/* 1. Build prototype */

		for(int j = 0; j < this.height; j ++) {
			for(int i = 0; i < this.width; i++) {
				// "clears" the first layer
				// adds a center path layer, in the shape of a cube outline
				// this will act as the first layer in building the level
				if ( super.checkLayer(i,j,TEMPLATE_SCALER,0) ) this.m.addGround(i,j); 
				else this.m.addWall(i,j);
			}
		}

		/* 2. Adding templates - selected by layer (from inside to out) */

		int layer1Temps = -1;
		int layer2Temps = -1;
		int layer3Temps = -1;


		// only place 9

		if(this.level == Generator.EASY) {
			layer1Temps = this.rand.getRandIntRange(0,2);
			layer2Temps = this.rand.getRandIntRange(1,2);
			layer3Temps = this.rand.getRandIntRange(0,2);
		} else if(this.level == Generator.MEDIUM) {
			layer1Temps = this.rand.getRandIntRange(1,2);
			layer2Temps = this.rand.getRandIntRange(1,2);
			layer3Temps = this.rand.getRandIntRange(1,3);
		} else if(this.level == Generator.HARD) {
			layer1Temps = this.rand.getRandIntRange(1,2);
			layer2Temps = this.rand.getRandIntRange(1,2);
			layer3Temps = this.rand.getRandIntRange(1,4);
		}

		addTemplates(layer1Temps,0);
		//super.clearPrevTemplate();

		addTemplates(layer2Temps,1);

		addTemplates(layer3Temps,2);

		//super.clearPrevTemplate();
		
		//addTemplates(1,2);


		/* 3. Placing items / Evaluation */
		int boxNum = this.level;

		boolean placed = false;
		if( placeCharacter() ) {
			
			this.numBadLocs = 0;

			int boxesPlaced = 0;
			for(int n = 0; n <= MAX_CHECKS; n++) {

				if(placeBox() == true) {
					// box and goal have been placed
					boxesPlaced++;
				}

				if (boxesPlaced == boxNum) {
					if(this.level == Generator.HARD && this.numBadLocs > (Generator.HARD-1)) placed = false;
					else placed = !(this.level != Generator.HARD && this.numBadLocs != 0);
					break;
				}

			}
			if(placed == false) this.m.clearEndLocs();

			return placed;

		} else {
			System.out.println("trying again...");
			this.numBadLocs = 0;
			return false;
		}
	}



	/** 
     * addTemplates method - randomly places a randomly rotated template
     * 
     * @param numOf - number of templates to place in a layer
     *		  layer - the layer of the map to add the template to (layers from center outwards)
     * @pre numOf > 0, layer == 0 or 1 or 2
     * @post a number of templates have been placed evaluation done by checking for no overlap and within layer
     *		  
     */
	private void addTemplates(int numOf, int layer) {

		int n = 0;

		int k = numOf; 
		while(k != 0) {

			int x = this.rand.getRandIntRange( Generator.TEMPLATE_SCALER-1, super.width-Generator.TEMPLATE_SCALER+1);
			int y = this.rand.getRandIntRange( Generator.TEMPLATE_SCALER-1, super.height-Generator.TEMPLATE_SCALER+1);

			if( super.checkTemplateOverlap(x,y,1) && super.checkLayer(x,y,Generator.TEMPLATE_SCALER,layer)) {
				int[] cords = {x,y};
				super.addPrevTemplate(cords);

				//System.out.println("Template Cords: "+ x +" "+ y);

				fillIn(x,y,MapGenerator.TEMPLATE_SCALER);
				k--;
			}
			
			if(n > Generator.MAX_CHECKS) {
				//System.out.println("Full up... ");
				break;
			}
			n++;
		}

		return;
	}

	/** 
     * fillIn method - overlays a randomly rotated/selected template onto the map, 
     *				   adding wall/ground based on the template
     * @param x - width positioning
     *		  y - height positioning
     *		  scaler - template size scaler
     * @pre x,y are valid cords, and not on the very edge of the map
     * @post wall/ground items will be placed depending on the template
     */
	private void fillIn(int x, int y, int scaler) {

		int[][] temp = super.getTemplate( this.rand.getRandInt(super.getTemplateListSize() ));

		int[][] newShape = super.rotateTemplate( temp, scaler, this.rand.getRandInt(4) );

		int u = 0; 
		for(int j = y- scaler /2; j <= y + scaler /2; j ++) {
			int v = 0;
			for(int i = x- scaler /2; i <= x + scaler /2; i++) {
				int check = -1;
				try{
					check = newShape[u][v];
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}

				if( check == 1 ) {
					try {
						m.addWall(i,j);
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}	
				} else if (check == 0  && !(i == 0 || i == this.width-1 || j == 0 || j == this.width-1) ){
					try {
						m.addGround(i,j);
					} catch(ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}	
				}				
				v++;
			}
			u++;
		}
	}

	

	/** 
     * placeCharacter method - tries to randomly place the character, evaluation done by checkSurroundings 
     * 
     * @pre no character currently on map
     * @post spawns character if a coordinate is valid according to checkSurroundings and trys has not been exceeded 
     * @return true - placed character, false - trys exceeded max checks 
     */
	private boolean placeCharacter() {
		/* Places character */
		int x;
		int y;
		int trys = 0;
		do {
			x = this.rand.getRandIntRange(1,this.width-2);
			y = this.rand.getRandIntRange(1,this.height-2);
			
			if(trys > MAX_CHECKS) {
				// cannot place character
				return false;
			}

			trys++;

		} while( checkSurroundings(x,y,"Character") != true );
		this.m.spawnCharacter(x,y,true);
		return true;
	}

	/** 
     * placeBox method - tries to randomly place a Box, evaluation done by boxLogic 
     * 
     * @post places a box if a coordinate is valid according to boxLogic and trys has not been exceeded 
     * @return true - placed a box, false - trys exceeded max checks 
     */
	private boolean placeBox() {
		int x;
		int y;
		int trys = 0;
		do{
			x = this.rand.getRandIntRange(2,this.width-2);
			y = this.rand.getRandIntRange(2,this.height-2);

			if(trys > MAX_CHECKS) {
				return false;
			}

			trys++;

		} while ( boxLogic(x,y) != true );

		return true;
	}


	/** 
     * This method checks the surroundings of the node, given its coords
     * @param x - width positioning
     *        y - height positioning
     *		  type - the item we're checking for
     * @pre x, and y are within bounds of 2d array, type != null
	 * @return whether the (x,y) position is a valid place for a "type" node
     */	
	private boolean checkSurroundings(int x, int y, String type) {

		if( !this.m.whatsInside(x,y).equals("Nothing") ) return false;
		else {
			int surroundings = 0;

			for(int j = y-1; j < y+2; j++) {
				for(int i = x-1; j < x+2; j++) {
					
					if( i != x && j != y && this.m.whatsInside(i,j).equals("Wall")) {
						surroundings++;
					}
				}	
			}

			if (type.equals("Box") && surroundings <= 2 ) {
				return true;
			} else return type.equals("Character") && surroundings <= 7;
		}
	}


	/** 
     * boxLogic method - evaluates whether a box can be placed 
     * 
     * @param x - x cord
     * 		  y - y cord
     * @pre 1 < x < width-1, 1 < y < height-1 
     * @post if true - box and goal will have been placed, if false - nothing placed
     * @return true - if checkSurroundings determines the box location is valid
     *				  and if the box location has a 1 box buffer free
     *				  and if goal placer can find a valid location
     *		   false - if any above checks fail
     */
	private boolean boxLogic(int x, int y) {

		boolean result = false;

		if(checkSurroundings(x,y,"Box") == true && checkBoxOverlap(x,y,1) ) {
			this.m.addTempBox(x,y);

			/* note: goal placer is instantiated every time there we try to add a new box */
			GoalPlacer goalPlacer = new GoalPlacer(this.m);
			int[] bestPos = goalPlacer.bestGoalPos();
			
			if(bestPos != null) countInRowCol(bestPos);
			//System.out.println(this.numBadLocs);
			this.m.removeTempBox();				
			
			try {
				if(bestPos != null){
					if( this.m.whatsInside(bestPos[0],bestPos[1]).equals("Nothing") 
						&& checkBoxOverlap(bestPos[0],bestPos[1],1) ) {

						this.m.addEndLoc(bestPos[0],bestPos[1]);
						this.m.addBox(x,y);

						int[] cords = {x,y};
						this.prevBoxCords.add(cords);
						
						int[] gCords = {bestPos[0],bestPos[1]};
						this.prevGoalCords.add(gCords);

						result = true;
					} else {
						result = false;
					}	
					
				}
			} catch(NullPointerException e) {
				result = false;
			}
		}

		return result;
	}
	
	/** 
     * 
     * 
     * @param 
     * @pre  
     * @post 
     * @return 
     */
	private boolean countInRowCol(int[] endLoc){
		int x = m.getTempBoxCoords()[0];
		int y = m.getTempBoxCoords()[1];
		for(int i = x; i<m.getWidth(); i++){
			if(i == endLoc[0] && y == endLoc[1]){
				numBadLocs ++;
				return true;
			}
			if(m.whatsInside(i, y).equals("Wall"))break;
		}
		
		for(int i = x; i>=0; i--){
			if(i == endLoc[0] && y == endLoc[1]){
				numBadLocs ++;
				return true;
			}
			if(m.whatsInside(i, y).equals("Wall"))break;
		}
		
		for(int j = y; j<m.getHeight(); j++){
			if(x == endLoc[0] && j == endLoc[1]){
				numBadLocs ++;
				return true;
			}
			if(m.whatsInside(x, j).equals("Wall")) break;
		}
		
		for(int j = y; j>=0; j--){
			if(x == endLoc[0] && j == endLoc[1]){
				numBadLocs ++;
				return true;
			}
			if(m.whatsInside(x, j).equals("Wall")) break;
		}
		
		return false;
		
	}


	/** 
     * checkBoxOverlap method - checks the overlap of (x,y) vs previous box locations
     * 
     * @param x - width positioning
     *		  y - height positioning
     *		  r - buffer of overlap
     * @pre x,y are valid cords, and not on the very edge of the map
     * @return true - no overlap, false - overlap
     */
	private boolean checkBoxOverlap(int x, int y, int r) {

		for(int[] prev : this.prevBoxCords) {
			for(int j = prev[1]-r; j < prev[1]+r+1; j++) {
				for(int i = prev[0]-r; i < prev[0]+r+1; i++) {

					//System.out.println("cords: "+x+" "+y);
					if(i == x && j == y) {
						return false;
					}
				}	
			}
		}
		return true;
	}

}
