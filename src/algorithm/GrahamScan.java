package algorithm;

import geom.Point;
import geom.PointSet;
import gui.GUIConsole;
import gui.PointCanvas;
import gui.PointSetCollection;

import java.awt.Color;
import java.awt.Graphics;

import complexity.ComplexityAnalysis;
import complexity.ComplexityOperation;


public class GrahamScan extends CHAlgorithm{
	
	public GrahamScan(GUIConsole console, ComplexityAnalysis complexity, PointCanvas canvas, PointSetCollection sets, int sleepTime, boolean animate){
		super (console, complexity, canvas, sets, sleepTime, animate);
	}

	public void prepareComplexityAnalyzer(){
		complexity.putOperationType("Find extreme point", ComplexityOperation.linear("n"), .25);
		complexity.putOperationType("Radial Sort", ComplexityOperation.nlogn("n"), 4);
		complexity.putOperationType("Evaluate Turn Direction", ComplexityOperation.constant(), .05);
	}
	
	public void run(){
		PointSet activePoints = sets.activePoints.get(0);
		
		if(activePoints.size() <= 3){
			finish();
			return;
		}

		console.println("Running the \"Graham Scan\" on a " + activePoints.size() + " point pointset.");
		
		PointSet internalPoints = new PointSet(Color.RED);
		sets.activePoints.add(internalPoints);
		
		activePoints.sortX();
		Point p0 = activePoints.remove(0);
		convexHull.add(p0);
		
		console.println("Found an extreme point at " + p0 + ".");
		complexity.putData("Find extreme point", new int[]{activePoints.size() + 1});
		relinquishControl();
		
		console.println("Sorting radial about " + p0 + ".");
		complexity.putData("Radial Sort", new int[]{activePoints.size() + 1});
		activePoints.sortRadial(p0, Math.PI * 3 / 2);
		
		canvas.repaint();
		relinquishControl();

		console.println("Adding first 2 points radially from " + p0 + ".");
		convexHull.add(activePoints.remove(0));

		canvas.repaint();
		relinquishControl();

		convexHull.add(activePoints.remove(0));

		canvas.repaint();
		relinquishControl();
		
		double oldTheta = p0.angleTo(convexHull.get(convexHull.size() - 1));
		while(true){
			//Check turn direction of last 3 points.
			boolean rightTurn = convexHull.get(convexHull.size() - 3).isLeftTurn(convexHull.get(convexHull.size() - 2), convexHull.get(convexHull.size() - 1));
			complexity.putData("Evaluate Turn Direction", new int[]{});
			
			//if good turn
			if(rightTurn){
				if(activePoints.size() == 0) break; //finished
				
				//Show sweep animation to next point, and then add it.
				double newTheta = p0.angleTo(activePoints.get(0));
				if(newTheta < oldTheta) oldTheta -= 2 * Math.PI;
				animateRadialSweep(p0, oldTheta, newTheta);
				
				oldTheta = newTheta;
				
				Point newHullPt = activePoints.remove(0);
				console.println("Tentatively added " + newHullPt + " to the convex hull.");
				convexHull.add(newHullPt); //add next

				relinquishControl();
			}
			else{
				//while bad turn, pop.
				do{
					Point poppedPt = convexHull.remove(convexHull.size() - 2);
					internalPoints.add(poppedPt);
					console.println("Popped " + poppedPt + ", because " + convexHull.get(convexHull.size() - 2) + ", " + poppedPt + ", and " + convexHull.get(convexHull.size() - 1) + " form a left turn");

					canvas.repaint();
					relinquishControl();
				}
				while(!convexHull.get(convexHull.size() - 3).isLeftTurn(convexHull.get(convexHull.size() - 2), convexHull.get(convexHull.size() - 1)));
			}
			//Bad turn
		}
		
		console.println("All points have been processed, the Graham Scan is complete.");
		
		sets.consolidateActivePoints();
		
		relinquishControl();
		
		finish();
	}

}