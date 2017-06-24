import java.util.Arrays;

public class Item{
	int[] pos;
	
	/**
	 * @param x coordinate
	 * @param y coordinate
	 * @post an Item will be instantiated at the specified location 
	 */
	public Item (int x, int y){
		pos = new int[2];
		pos [0] = x;
		pos [1] = y;
	}
	
	/**
	 * @return array containing location coordinates
	 */
	public int[] getLoc(){
		return pos;
	}
	
	/**
	 * @return x coordinate of the character
	 */
	public int getPosX(){
		return pos[0];
	}
	
	/**
	 * @return y coordinate of the character
	 */
	public int getPosY(){
		return pos[1];
	}

	/**
	 * @return string containing item type
	 */
	public String getItemName(){
		return this.getClass().getName();
	}
	
	
	/**
	 * @pre move must be legal
	 * @param constant denotes direction to move
	 * @post appropriate fields will be updated to reflect the move
	 */
	public void move(int moveDir){
		int addToX = 0;
		int addToY = 0;
		switch(moveDir){ //stores how the coordinate will change depending on input
			case MapRep.UP: 	
				addToY = -1;
		      	break;
			case MapRep.DOWN:	
				addToY = 1;
				break;
			case MapRep.LEFT: 
				addToX = -1;
				break;
			case MapRep.RIGHT:
				addToX = 1;
				break;
		}
		this.addToPosX(addToX);
		this.addToPosY(addToY);
	} 
	
	/**
	 * @param another item
	 * @return if they're in the same place
	 */
	@Override
	public boolean equals(Object i){
		if(((Item) i).getPosX() != this.getPosX()) return false;
		return ((Item) i).getPosY() == this.getPosY();
	}
	
	/**
	 * @return generate hashCode depending on coordinates
	 */
	@Override
	public int hashCode(){
		return Arrays.hashCode(getLoc());
	}
	
	/**
	 * @param value to add to x coordinate
	 */
	private void addToPosX(int x){
		pos[0] += x;
	}
	
	/**
	 * @param value to add to y coordinate
	 */
	private void addToPosY(int y){
		pos[1] += y;
	}
	
	
}
