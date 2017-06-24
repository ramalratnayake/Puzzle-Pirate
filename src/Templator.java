import java.util.List;
import java.util.ArrayList;

/**
 * Templator - builds, rotates and randomizes templates and their placement
 *
 * @author Oscar Downing (z5114817)
 */
public class Templator {

	public int width;
	public int height;

	//private RandomGenerator rand;

	private List<int[][]> templates;
	private List<int[]> prevTemplateCords;
	
	/** 
     * Templator constructor - instantiates the Templator class
     * 
     * @param width - width of map
     *		  height - height of map 
     * @pre rand != null, width >= 0 and height >= 0
     * @post Templator object is instantiated
     */
	public Templator(int width, int height) { //RandomGenerator rand, int width, int height) {

		this.width = width;
		this.height = height;

		/* Random setup */
		//this.rand = rand;
		
		/* Templates setup - centers templates to ensure they always connect */
		this.templates = new ArrayList< int[][] > ();

		int[][] a = { {0,0,0}, {0,1,0}, {0,0,0} };
		int[][] b = { {0,0,-1}, {0,0,-1}, {0,0,-1} };
		int[][] c = { {-1,0,0}, {-1,0,0}, {-1,-1,-1} };

		this.templates.add(a);
		this.templates.add(b);
		this.templates.add(c);

		this.prevTemplateCords = new ArrayList< int[] > ();
		
	}

	/** 
     * 
     * @param x - index of templates
     * @pre x >= 0
     * @return the template at index x
     */
	public int[][] getTemplate(int x) {
		return this.templates.get(x);
	}

	/** 
     * 
     * @pre templates != null
     * @return the template size
     */
	public int getTemplateListSize() {
		return this.templates.size();
	}

	/** 
     * @param cords - (x,y) position of template on the map 
     * @pre prevTemplatesCords != null, cords is a valid (x,y) position on map
     * @post cords is added to the list of previous template cords
     */
	public void addPrevTemplate(int[] cords) {
		this.prevTemplateCords.add(cords);
	}

	/** 
     * @pre prevTemplatesCords != null
     * @post template list is clear
     */
	public void clearPrevTemplate() {
		this.prevTemplateCords.clear();
	}



	/** 
     * checkTemplateOverlap method - checks the overlap of (x,y) vs previous template locations
     * 
     * @param x - width positioning
     *		  y - height positioning
     *		  r - buffer of overlap
     * @pre x,y are valid cords, and not on the very edge of the map
     * @return true - no overlap, false - overlap
     */
	public boolean checkTemplateOverlap(int x, int y, int r) {

		for(int[] prev : this.prevTemplateCords) {
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


	/** 
     * checkLayer method - checks whether a given cord is within a given layer
     * 
     * @param i - width positioning
     *		  j - height positioning
     *        scaler - template size scaler
     *        layer - given layer of the map (layers from center outwards)
     * @pre i,j are valid cords for the map, scaler > 0, layer > 0
     * @return true - within layer , false - outside of layer 
     */
	public boolean checkLayer(int i, int j, int scaler, int layer) {

		int xScaler = 1+layer;
		int yScaler = 1+layer;

		if( i >= (double) this.width / 2 - (xScaler + 1)
				&& i <= (double) this.width / 2 + xScaler
				&& (j == (double) this.height / 2 + xScaler
				|| j == (double) this.height / 2 - (xScaler + 1))

				|| j >= (double) this.height / 2 - (yScaler + 1)
				&& j <= (double) this.height / 2 + yScaler
				&& (i == (double) this.width / 2 + yScaler
				|| i == (double) this.width / 2 - (yScaler + 1)) ) return true;

		else return false;
	}

	/** 
     * rotateTemplate method - rotates a given 2d array by n times
     * 
     * @param map - 2d template array rep
     *		  scaler - template size scaler
     *		  n - times to rotate
     * @pre map != null, scaler > 0, n >= 0; 
     * @return the template rotated n times
     */
	public int[][] rotateTemplate(int[][] map, int scaler, int n) {

		int[][] rot = new int[scaler][scaler];

		for(int i = 0; i < scaler; ++i) {
			for(int j = 0; j < scaler; ++j) {
				rot[i][j] = map[scaler-j-1][i];
			}
		}

		if(n == 0) {
			return rot;
		} else {
			return rotateTemplate(rot,scaler,n-1);
		}

	}


}