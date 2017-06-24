import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class GoalPlacer {

	private PriorityQueue<MapRep> open;
	private Set<MapRep> closed;
	private MapRep original;
	private int[] tempBox;
	private List<MapRep> goalStates;
	private int[] prevPos;
	
	static final int numPossibleMoves = 4;
	/**
	 * @param map representation object to set end location for
	 * @post GoalPlacer object instantiated
	 */
	public GoalPlacer(MapRep m){
		this.original = m;	
		open = new PriorityQueue<MapRep>();
		closed = new HashSet<MapRep>();
		goalStates = new ArrayList<MapRep>();
		
		if(prevPos != null){
			original.removePlayers();
			original.spawnCharacter(prevPos[0], prevPos[0], true);
		}
	}
	
	/**
	 * @pre correctly instantiated (m=map representation saved)
	 * @return if solvable,coordinates of best location to place goal in, else null 
	 */
	public int[] bestGoalPos(){
		tempBox = original.getTempBoxCoords();
		MapRep bestState = original.cloneState();
		bestState.setIsSim();
		bestState.loadHeuristic(new DistsToNewGoal(bestState, tempBox));
		MapRep polled;
		MapRep temp; 
		open.add(bestState);
		
		/*conduct A* push traversal with a heuristic that 
		directs the box as far as possible from initial location*/
		do{
			polled = open.poll();
			
			for(int i = 0; i < numPossibleMoves; i++){ 
				temp = polled.cloneState();
				// the best state has lowest heuristic (goal is farthest away from box)
				if(polled.getHeuristic() < bestState.getHeuristic()){
					bestState = polled;
				}
				//spawns character at all possible locations (4 of them) around box
				if(!spawnNextToBox(i, temp)){
					continue;
				}
				
				//character tries to push the box
				if(temp.moveRequest(towardsBox(temp))){
					// if successful then store in queue with new heuristic value
					temp.loadHeuristic(new DistsToNewGoal(temp, tempBox));
					if(closed.contains(temp)) continue; //don't consider if already seen
					open.add(temp);
				}
			}
			closed.add(polled);
		}	
		while (!open.isEmpty());
		
		// few more checks to see whether goal placed is at an interesting place
		if(!isProperSolution(bestState)) return null;

		//adds this state to a list for later if more boxes goals needed
		goalStates.add(bestState); 
		prevPos = bestState.getPlayerCoord();
		
		//current position of temporary box is where goal should be placed
		int[] coords = bestState.getTempBoxCoords();
		
		return coords;
	}

	
	/**
	 * tries to spawn character in certain location and checks whether that location is reachable from current
	 * @pre pos should be 1 - 4 to spawn in all locations
	 * @param pos to try spawn (i.e. up, down, left, right)
	 * @param map representation that is currently used
	 * @return whether the character successfully spawned in location 
	 */
	private boolean spawnNextToBox(int pos, MapRep map){
		int[] currentPos = map.getPlayerCoord();
		map.removePlayers();
		int x = map.getTempBoxCoords()[0];
		int y = map.getTempBoxCoords()[1];


		if(pos == 0 && (map.whatsInside(x-1, y).equals("Nothing"))){ //if nothings there then spawn
			map.spawnCharacter(x - 1, y, true);
			map.setPushCoord(x - 1, y); //set the coordinate that the character pushed a box
			if(this.canGetThere(map, currentPos)) { //check if that location is reachable
				if(areOthersReachable(map)) return true; //check if all other boxes are reachable
			}

		}
		else if(pos == 1 && (map.whatsInside(x+1, y).equals("Nothing"))){
			map.spawnCharacter(x + 1,y, true);
			map.setPushCoord(x + 1, y);
			if(this.canGetThere(map,currentPos)){
				if(areOthersReachable(map)) return true;
			}

		}
		else if(pos == 2 && (map.whatsInside(x, y-1).equals("Nothing"))){
			map.spawnCharacter(x,y - 1, true);
			map.setPushCoord(x,y - 1);
			if(this.canGetThere(map,currentPos)){
				if(areOthersReachable(map)) return true;
			}

		}
		else if(pos == 3 && (map.whatsInside(x, y+1).equals("Nothing"))){
			map.spawnCharacter(x, y + 1, true);
			map.setPushCoord(x,y + 1);
			if(this.canGetThere(map,currentPos)){
				if(areOthersReachable(map)) return true;
			}

		}
		
		return false;
	}
	
	/**
	 * @param map representation that is currently used
	 * @return constant denoting direction to move in order to push box
	 */
	private int towardsBox(MapRep map){ //move in a direction that pushes the box
		int[] diff = new int[2];
		diff[0] = map.getPlayerCoord()[0] - map.getTempBoxCoords()[0];
		diff[1] = map.getPlayerCoord()[1] - map.getTempBoxCoords()[1];
		
		if(diff[0] == -1) return MapRep.RIGHT;
		
		if(diff[0] ==  1) return MapRep.LEFT;
		
		if(diff[1] == -1) return MapRep.DOWN;
		
		if(diff[1] ==  1) return MapRep.UP;

		return 0;
	}
	
	/**
	 * 
	 * @param map representation that is currently used
	 * @param the player item that used to be there 
	 * @return if the new location can be reached from current 
	 */
	private boolean canGetThere(MapRep map, int[] originalPlayer){
		Queue<MapRep> toVisit = new PriorityQueue<MapRep>();
		Set<MapRep> visited = new HashSet<MapRep>();
		
		MapRep temp = map.cloneState();
		MapRep poll;
		
		temp.setIsSim(); 
		temp.playerNoPush(); //change a few method behaviors in map class
		
		/*
		 * Conducts an A* search to find a path to new position with Manhattan distance heuristic
		 * Keeps adding every possibility of movement to the priority queue
		 */
		toVisit.add(temp);
		do{
			poll = toVisit.poll();
			poll.playerNoPush();
			if (Arrays.equals(poll.getPlayerCoord(), originalPlayer)) return true;
			
			temp = poll.cloneState();
			
			if(temp.moveRequest(MapRep.DOWN) && !visited.contains(temp)) {
				temp.loadHeuristic(new TowardsNewLoc(temp, originalPlayer));
				toVisit.add(temp);
			}
			
			temp = poll.cloneState();
			if(temp.moveRequest(MapRep.UP) && !visited.contains(temp)){
				temp.loadHeuristic(new TowardsNewLoc(temp, originalPlayer));
				toVisit.add(temp);
			}
			
			temp = poll.cloneState();
			if(temp.moveRequest(MapRep.LEFT) && !visited.contains(temp)){
				temp.loadHeuristic(new TowardsNewLoc(temp, originalPlayer));
				toVisit.add(temp);
			}
			
			temp = poll.cloneState();
			if(temp.moveRequest(MapRep.RIGHT) && !visited.contains(temp)){
				temp.loadHeuristic(new TowardsNewLoc(temp, originalPlayer));
				toVisit.add(temp);
			}
			
			visited.add(poll);
		}
		while(!toVisit.isEmpty());
		
		return false;
	}
	
	/**
	 * checks whether if all 'first push' locations (saved above) of other boxes are reachable
	 * as it ensures that the other boxes can still be successfully pushed into a goal
	 * @param map thats currently being worked on
	 * @return if all other boxes are still reachable
	 */
	private boolean areOthersReachable(MapRep map){
		MapRep m = map.cloneState();
		Iterator<MapRep> it = this.goalStates.iterator();
		int[] toCheck;
		MapRep temp;
		
		while(it.hasNext()){
			temp = it.next();
			toCheck = temp.getFirstPushCoord();
			
			if(toCheck == null) continue;

			m.playerNoPush();
			if(!canGetThere(m, toCheck)) return false;
		}
		
		return true;
	}

	/**
	 * @pre temporary box still exists
	 * @param bestState that placed the temporary box as far as possible
	 * @return whether the solution satisfies certain conditions
	 */
	private boolean isProperSolution(MapRep bestState){
		int[] newBox = bestState.getTempBoxCoords();
		
		if(Arrays.equals(newBox, tempBox)) return false; //solution not in the same place
				
		//solution at least two spaces away
		if(Math.abs(newBox[0] - tempBox[0]) < 2){
			if(Math.abs(newBox[1] - tempBox[1]) < 2) return false;
		}
		
		return true;
	}
	
}
 