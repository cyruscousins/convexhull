package algorithm;

import gui.Main;

//Controls a CH algorithm thread.
public class RealTimeCHAlgorithmController implements CHAlgorithmController{
	Main main;
	public CHAlgorithm algorithm;

	public CHAlgorithm getAlgorithm(){
		return algorithm;
	}
	
	public boolean terminated, stopped, paused;
	
	public RealTimeCHAlgorithmController(Main main, CHAlgorithm algorithm){
		this.main = main;
		this.algorithm = algorithm;
	}

	public static final int MODE_SLEEP = 0, MODE_WAIT = 1, MODE_CONTINUOUS = 2;
	int algorithmMode = MODE_SLEEP;
	
	public void run(){
		algorithm.thread = new Thread(algorithm);
		algorithm.thread.start();

		out:
		while(!algorithm.isFinished() && !terminated){
			while(paused || stopped){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(terminated) break out;
			}
			algorithm.go();
			//TODO we need to wait for the algorithm thread to suspend.
			int mode = algorithmMode, time = algorithm.sleepTime;
			if(algorithm.inAnimation){
				mode = MODE_SLEEP;
				time = algorithm.animSleepTime;
			}
			switch (mode) {
				case MODE_SLEEP:
					try{
						Thread.sleep(time);
					}
					catch(Exception e){
						e.printStackTrace();
					}
					break;
				case MODE_WAIT:
					paused = true;
					break;
				case MODE_CONTINUOUS:
					break;
			}
		}
		if(terminated){
			algorithm.cleanup();
			main.gui.repaint();
		}
		main.gui.activateGUI();
	}
	
	public void pause(){
		algorithmMode = MODE_WAIT;
		paused = true;
	}
	
	public void unpause(){
		algorithmMode = MODE_SLEEP;
		paused = false;
	}
	
	public void step(){
		paused = false;
	}
	
	public void skip(){
		algorithmMode = MODE_CONTINUOUS;
	}
	
	public void terminate(){
		terminated = true;
	}
}
