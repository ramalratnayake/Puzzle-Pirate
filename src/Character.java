public class Character extends Item{
	
	private int currentDirection;
	private boolean noPush;
	
	public Character (int x, int y){
		super (x,y);
		noPush = false;
	}
			
	/**
	 * @param newDir is a constant denoting what direction to move in 
	 * @post coordinate & current direction will be changed accordingly
	 */
	public void moveInDirection(int newDir){
		currentDirection = newDir;
		this.move(newDir);
	}
	
	/**
	 * @return a deep copy of the character
	 */
	public Character clone(){
		Character cloned = new Character (this.getPosX(), this.getPosY());
		cloned.currentDirection = this.currentDirection;
		cloned.noPush = this.noPush;
		return cloned;
	}
	
	/**
	 * @return a deep copy of the character which has been moved
	 */
	public Character cloneWithNewLoc(int moveDir){
		Character cloned = new Character (this.getPosX(), this.getPosY());
		cloned.move(moveDir);
		cloned.currentDirection = this.currentDirection;
		cloned.noPush = this.noPush;
		return cloned;
	}


	/**
	 * @return if the player is allowed to push boxes
	 */
	public boolean canPush(){
		return !this.noPush;
	}
	
	/**
	 * @post makes the character unable to push any boxes (for solvers)
	 */
	public void setNoPush(){
		this.noPush = true;
	}
	
	@Override
	public boolean equals(Object o){
		if(!super.equals(o)) return false;
		
		if(!noPush){
			Character c = (Character)o;
			
			return (this.currentDirection == c.currentDirection);
		}
		
		return true;
	}
	
}
