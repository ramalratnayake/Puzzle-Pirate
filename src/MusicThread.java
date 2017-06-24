import java.io.*;
import javax.sound.sampled.*;


public class MusicThread extends Thread {
	private String song;
	private	Clip clip;
	private boolean isTrue;


	
	public MusicThread(String song){
		this.song = song;
		this.isTrue = true;
	}


	
	public void run(){
		if(!isTrue){
			clip.close();
			System.out.println("complete");
			return;
		}
		//play song 
		try{
				AudioInputStream audio = AudioSystem.getAudioInputStream(new File(song));
		 		clip = AudioSystem.getClip();
				clip.open(audio);
				clip.start();
				//while(isTrue){
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				
		}
		 catch(UnsupportedAudioFileException e) {
			 e.printStackTrace();
		 } catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


	}

	public void mute(){
		if(isTrue == false){
			isTrue = true;
			run();
		}
		else{
		isTrue = false;
		System.out.println("changed");
		run();
		}
	}

	public void close(){
		isTrue = false; 
		run();
	}
}

