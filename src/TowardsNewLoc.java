
public class TowardsNewLoc implements MapRepHeuristic{
	int[] from;
	int[] to;
	public TowardsNewLoc(MapRep m, int[] original){
		from = original;
		to = m.getPlayerCoord();
	}
	
	@Override
	public int heuristicVal() {
		// return the Manhattan distance
		int distBetween = Math.abs(from[0] - to[0]) + Math.abs(from[1] - to[1]);
		return distBetween;
	}

}
