public class DistsToNewGoal implements MapRepHeuristic {
	private MapRep m;
	private int stX;
	private int stY;
	private int x;
	private int y;
	
	public DistsToNewGoal(MapRep map, int [] boxPlace){
		this.m = map;
		stX = boxPlace[0];
		stY = boxPlace[1];
		x = map.getTempBoxCoords()[0];
		y = map.getTempBoxCoords()[1];
	}
	
	@Override
	public int heuristicVal() {
		int totalDist = 0;
		int currDist = 0;
		int xDiff = 0;
		int yDiff = 0;
		
		for(int i = 0; i < m.getWidth(); i++){
			for(int j = 0; j<m.getHeight(); j++){
				//maximize distance between goal and all other goals, boxes and start location
				if(m.whatsInside(i, j).equals("EndLoc") || m.whatsInside(i, j).equals("Box") || (i==stX && j == stY)){
					xDiff = Math.abs(i - x);
					yDiff = Math.abs(j - y);
					currDist = xDiff + yDiff;
					totalDist += currDist;
				}
			}
		}
		// the farthest location for a box will be sorted to the start of a pqueue
		return Integer.MAX_VALUE - totalDist;
	}

}
