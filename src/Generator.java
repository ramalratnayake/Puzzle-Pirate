/**
 * Map Generator interface 
 *
 */

public interface Generator {

	int TEMPLATE_SCALER = 3;

	int MAX_CHECKS = 144;

	int EASY = 1;
	int MEDIUM = 2;
	int HARD = 3;

	boolean generateMap();

}