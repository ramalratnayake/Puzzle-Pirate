import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Queue;

/** 
 * Map generator thread 
 *
 */	

public class GeneratorThread implements Runnable{

	private Thread t;
	private int level;
	private ArrayBlockingQueue<MapRep> mapQueue;

	/** 
     * Generator
     *
     * @param
     * @pre 
     * @post
     * @return
     */	
	public GeneratorThread(int level, ArrayBlockingQueue<MapRep> mapQueue) {
		this.t = null;
		this.level = level;
		this.mapQueue = mapQueue;
	}

	/* === Methods === */

	/** 
     * 
     *
     * @param
     * @pre 
     * @post
     * @return
     */	
	@Override
	public void run() {
	    try {
	    	
	    	while(true) {

		    	MapRep m = new MapRep(12,12,this.level, false);
			   	this.mapQueue.put(m);
			   	Thread.sleep(250);
			}
	    }catch (InterruptedException e) {
	         System.out.println("Thread interrupted.");
	    }
	}

	public void start() {
		if(this.t == null) {
			this.t = new Thread(this,"Map Gen Thread");
			this.t.start();
		}
	}


}