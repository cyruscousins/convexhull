package algorithm;

import geom.Point;
import geom.PointSet;
import gui.GUIConsole;
import gui.PointCanvas;
import gui.PointSetCollection;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import complexity.ComplexityAnalysis;


public abstract class CHAlgorithm implements Runnable{
	Thread thread;

	GUIConsole console;
	ComplexityAnalysis complexity;
	
	PointCanvas canvas;
	PointSetCollection sets;
	
	PointSet activePoints;
	PointSet convexHull;

	int sleepTime;
	
	boolean animated;

	int animSleepTime = 1000 / 30;
	public boolean inAnimation;
	
	Color originalColor;  //Keep track of the color of the active pointset.
	
	public CHAlgorithm(GUIConsole console, ComplexityAnalysis complexity, PointCanvas canvas, PointSetCollection set, int sleepTime, boolean animated){
		this.console = console;
		this.canvas = canvas;
		this.sets = set;
		this.sleepTime = sleepTime;
		this.animated = animated;
		
		activePoints = set.activePoints.get(0);
		
		convexHull = new PointSet(Color.ORANGE);
		set.convexHull = new ArrayList<PointSet>();
		set.convexHull.add(convexHull);
		
		sweeperLen = (int)(Math.sqrt(1 + canvas.width * canvas.width + canvas.height * canvas.height));
		
		originalColor = set.activePoints.get(0).color;
		
		this.complexity = complexity;
		complexity.reset();
		prepareComplexityAnalyzer();
	}
	
	public boolean checkBaseCase(){
		if(activePoints.size() <= 3){
			while(!activePoints.isEmpty()){
				convexHull.add(activePoints.remove(activePoints.size() - 1));
			}
			return true;
		}
		else return false;
	}
	
	public abstract void prepareComplexityAnalyzer();
	
	boolean on = false;
	public void go(){
//		on = true;
		thread.resume();
	}
	public void relinquishControl(){
		canvas.parent.analysisPanel.complexity.updatePanel();
		if(!animated) return;
		canvas.repaint();
//		while(!on){
//			thread.suspend();
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		thread.suspend();
	}
	
	boolean finished = false;
	protected void finish(){
		finished = true;
		
		//Restore state.
		sets.consolidateActivePoints();
		sets.activePoints.get(0).removeDuplicates();
		sets.activePoints.get(0).color = originalColor;
		
		//Unanimated algorithms render upon completion
		if(!animated){
			canvas.repaint();
		}
	}
	
	boolean isFinished(){
		return finished;
	}
	
	float dtheta = .02f;
	int sweeperLen = 800;
	void animateRadialSweep(Point p0, double theta0, double theta1){
		if(!animated) return;
		inAnimation = true;
		PointSet sweeper = new PointSet(Color.BLUE);
		sets.sweeper = sweeper;
		sweeper.add(p0);
		Point p1 = new Point(p0.x, p0.y);
		sweeper.add(p1);
		
		if(theta1 < theta0){
			while(theta1 < theta0){
				p1.x = p0.x + (int)(sweeperLen * Math.cos(theta0));
				p1.y = p0.y + (int)(sweeperLen * Math.sin(theta0));
				relinquishControl();
				theta0 -= dtheta;
			}
		}
		else if(theta0 < theta1){
			while(theta0 < theta1){
				p1.x = p0.x + (int)(sweeperLen * Math.cos(theta0));
				p1.y = p0.y + (int)(sweeperLen * Math.sin(theta0));
				relinquishControl();
				theta0 += dtheta;
			}
		}
		sweeper.clear();
		inAnimation = false;
	}
	
	//This function should be called when the algorithm finished abnormally (ie is stopped by the user).
	public void cleanup(){
		//Do some cleanup.
		sets.consolidateActivePoints();
		sets.addConvexHullPointsToActivePoints();
		sets.activePoints.get(0).removeDuplicates();
		sets.activePoints.get(0).color = originalColor;
		sets.pairedPoints.clear();
		sets.sweeper = null;

		sets.numbering = sets.fading = false;
	}
}
