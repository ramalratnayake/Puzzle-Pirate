public class Node{
    private Item storedHere;
    private Node [] adjacentNodes;
    
    final int UP = 0;
    final int DOWN = 1;
    final int LEFT = 2;
    final int RIGHT = 3;
    
    /**
     * @post instantiates a node
     */
    public Node (){
        storedHere = null;
        adjacentNodes = new Node [4]; //up, down, left, right
    }   
    
    /**
     * 
     * @param node above
     * @param node below
     * @param node to the left
     * @param node to the right
     * @post adjacent nodes will be set
     */
    public void setAdjacent(Node up, Node down, Node left, Node right){
        adjacentNodes[this.UP] = up;
        adjacentNodes[this.DOWN] = down;
        adjacentNodes[this.LEFT] = left;
        adjacentNodes[this.RIGHT] = right;              
    }

    
    /**
     * @param moveDirection
     * @param item to move in
     * @param sim - if its in simulation mode
     * @return if the object is able to move into this node
     */
    public boolean tryMoveIn(int moveDirection, Item item, boolean sim){
    	boolean success = true;
    	
    	//if wall no need to check anything else
    	if(this.whatsInside().equals("Wall")) success = false;
    	
    	else if(item.getItemName().equals("Character")){
    		String whatsInside = this.whatsInside();
    		
    		if(!((Character) item).canPush()){ //if the player is unable to push any boxes then reject
    			if(whatsInside.equals("TempBox") || whatsInside.equals("Box")) return false;
    		}
    		
    		/* if not empty or a goal location, then recursively check if 
    		 * box/temporary box can be pushed into appropriate adjacent node
    		 */
    		if(!(whatsInside.equals("Nothing")) && !(whatsInside.equals("EndLoc"))){
    			return getAdjacent(moveDirection).tryMoveIn(moveDirection, storedHere, sim);
    		}
    	}
    	
    	// a box can only enter a node containing nothing or goal 
    	else if(item.getItemName().equals("Box")){
            success = this.whatsInside().equals("Nothing") || this.whatsInside().equals("EndLoc");
    	}
    	
    	// a temporary box (during simulation) can only enter empty nodes
    	else if(item.getItemName().equals("TempBox")){
            success = this.whatsInside().equals("Nothing");
    	}
    	
    	return success;
    }
    
    /**
     * @return string representing what the node contains
     */
    public String whatsInside(){
        if(storedHere == null) return "Nothing";
        return this.storedHere.getClass().getName();
    }
    
    /**
     * @post node will no longer contain anything
     * @return the Item that the node previously contained
     */
    public Item moveItemOut(){
    	Item ret = storedHere;
        this.storedHere = null;
        return ret;
    }
    
    /**
     * @pre the function "canIMoveHere" must be true
     * @param item to move into node
     * @param moveDirection
     * @post item stored in node, if box in node it'll be pushed to adjacent node
     */
    public void moveItemIn(Item item, int moveDirection){
    	if(storedHere == null) {
    		storedHere = item; //nothing inside so just store
    		return;
    	}
    	
    	else if(whatsInside().equals("Box") || whatsInside().equals("TempBox")){
            storedHere.move(moveDirection); //otherwise have to push the box
            
            //if just placing a box then direction will be nowhere hence no need pushing
            if(moveDirection != MapRep.NOWHERE){ 
            	getAdjacent(moveDirection).moveItemIn(storedHere, moveDirection);
            }
        }
        storedHere = item; //there is nothing here so just store item
    }
    
    /**
     * @param direction of desired adjacent node
     * @return the node in that direction
     */
    private Node getAdjacent (int direction){
        switch (direction){
            case MapRep.UP:
                return this.adjacentNodes[this.UP];
            case MapRep.DOWN:
                return this.adjacentNodes[this.DOWN];
            case MapRep.LEFT:
                return this.adjacentNodes[this.LEFT];
            case MapRep.RIGHT:
                return this.adjacentNodes[this.RIGHT];
        }
        return null;
    }
}
