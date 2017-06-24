import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class Solver{
    MapRep start;
    static final int numPossibleMoves = 4;
    
    public Solver(MapRep m) {
        start = m.cloneState();
        start.clearMoves();
        start.setIsSim();
    }
    
    /**
     * A* map solver
     * @post - map representation to work already saved in class
     * @return list of constants denoting moves if solution available, else null
     */
    public List <Integer> getSolution() {
        List <Integer> soln = new ArrayList < Integer > ();
        
        PriorityQueue <MapRep> open = new PriorityQueue <MapRep>();
        Set <MapRep> closed = new HashSet <MapRep >();
        MapRep poll;
        MapRep temp = null;
        start.loadHeuristic(new DistToEverything(start));
        
        open.add(start);
        
        while(!open.isEmpty()){
        	
            poll = open.poll();
            
           
            if (poll.isGameFinished()) {
                return statesToMoves(poll);
            }
            temp = poll.cloneState();

            	closed.add(poll);
            	
            	for(int i = 0; i < numPossibleMoves; i++){
            		temp = poll.cloneState();
            		if(temp.moveRequest(moveDir(i))){
            			//System.out.println("(" + temp.getPlayer().getPosX() + "," + temp.getPlayer().getPosY() + ")");
            			temp.loadHeuristic(new DistToEverything(temp)); 
            			
            			if(!open.contains(temp) && !closed.contains(temp)){
            				open.add(temp);
            			}
            			
            			else{
            				//System.out.println("found");
            				Iterator <MapRep> it = open.iterator();
            				
            				while(it.hasNext()){
            					MapRep m;
            					m = it.next();
            					
            					if(m.equals(temp)){
            						if(m.getHeuristic() > temp.getHeuristic()){
            							open.add(temp);
            						}
            					}
            				}
            			}
            		}
            	}
        }
        System.out.println(":(");
        
        return null; //no solution
    }
    
    /**
     * @param m - map to work on
     * @pre the map can return the state list
     * @return the list of states converted into moves
     */
    private List <Integer> statesToMoves(MapRep m) {
        Iterator <MapRep> it = m.prevMovesIterator();
        List <Integer> moves = new ArrayList <Integer>();
        
        while (it.hasNext()) {
        	moves.add((it.next()).getLastMove());
        }
       // moves.add(m.getPlayer().getDirection());
        
        System.out.println();
        Iterator < Integer > i = moves.iterator();
        
        while (i.hasNext()) {
            switch (i.next()) {
                case MapRep.DOWN:
                    System.out.print("D, ");
                    break;
                case MapRep.UP:
                    System.out.print("U, ");
                    break;
                case MapRep.LEFT:
                    System.out.print("L, ");
                    break;
                case MapRep.RIGHT:
                    System.out.print("R, ");
                    break;
            }
        }
        
       // System.out.println();
        return moves;
    }
    
    /**
     * @param i - integer from 0 to 3
     * @return a Map Rep constant denoting direction
     */
    private int moveDir(int i){
    	switch (i){
    		case 0 : return MapRep.UP;
    		case 1 : return MapRep.LEFT;
    		case 2 : return MapRep.DOWN;
    		case 3 : return MapRep.RIGHT;
    	}
    	return MapRep.NOWHERE;
    }
    
   
}