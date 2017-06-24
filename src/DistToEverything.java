import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 

 
public class DistToEverything implements MapRepHeuristic {
 
  MapRep map;
 
  public DistToEverything(MapRep m){
    map = m;
  }
 
  
  /**
   * @return heuristic value that considers distance of player to boxes
   * 		and minimum distance from box to goal
   */
  @Override
  public int heuristicVal() {
	  int result = 0;
	  
	  List<int[]> boxes = new ArrayList<int[]>();
	  List<int[]> goals = new ArrayList<int[]>();
	  
	  for(int i = 0; i < map.getWidth(); i++){
		  
		  for(int j = 0; j<map.getHeight(); j++){
			  int[] coords = {i,j};
			  
			  switch (map.whatsInside(i, j)) {
		  			case "Box": 	boxes.add(coords);
	  								break;
		  			case "EndLoc":	goals.add(coords);
		  							break;
			  }
		  }
	  }
	  
	  Iterator <int[]> boxIt = boxes.iterator();
	  
	  int[] boxCoord;
	  int[] goalCoord;
	  int[] playerCoord = map.getPlayerCoord();
	  
	  while(boxIt.hasNext()){
		  boxCoord = boxIt.next();
		  result += manhattanDist(boxCoord, playerCoord);
		  //System.out.println("after player: " + result);
		  Iterator <int[]> goalIt = goals.iterator();
		  int minDist = Integer.MAX_VALUE;
		  //System.out.println("compare (" + goalCoord[]);
		  while(goalIt.hasNext()){
			  goalCoord = goalIt.next();
			  
			  //System.out.println(manhattanDist(goalCoord, boxCoord));
			  if(manhattanDist(goalCoord, boxCoord) < minDist){
				  minDist = manhattanDist(goalCoord, boxCoord);
			  }
		  }
		  
		  result += minDist;  
	  }	
	  
	  //System.out.println("after boxes: " + result);
	  
	  //System.out.println(result);
 
    return result;
 
  }
 
  
 /**
  * 
  * @param i - item 1 coordinates
  * @param j - item 2 coordinates
  * @return Manhattan distance between two coordinates
  */
  private int manhattanDist(int[] i, int[] j){
	  return (Math.abs(i[0] - j[0]) + Math.abs(i[1] - j[1]) ); 
 
  }
 

 
}
