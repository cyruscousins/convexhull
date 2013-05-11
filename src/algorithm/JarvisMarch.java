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


public class JarvisMarch extends CHAlgorithm{
	
	public JarvisMarch(GUIConsole console, ComplexityAnalysis complexity, PointCanvas canvas, PointSetCollection sets, int sleepTime, boolean animate){
		super (console, complexity, canvas, sets, sleepTime, animate);
	}
	
	public void prepareComplexityAnalyzer(){
		complexity.putOperationType("Find extreme point", ComplexityOperation.linear("n"), .25);
		complexity.putOperationType("Radial Sort", ComplexityOperation.nlogn("n"), 4);	
	}
	
	public void run(){
		PointSet activePoints = sets.activePoints.get(0);
		
		if(checkBaseCase()){
			finish();
			return;
		}
		
		console.println("Running the \"Jarvis March Convex Hull Algorithm\" on a " + activePoints.size() + " point pointset.");
		
		activePoints.sortX();
		Point p0 = activePoints.get(0);
		convexHull.add(p0);

		complexity.putData("Find extreme point", new int[]{activePoints.size()});
		console.println("Found leftmost point at " + p0 + " (any extreme point will work).");
		relinquishControl();
		
		sets.numbering = sets.fading = true;
		
		double oldTheta = -Math.PI / 2;
		double newTheta;
		double angle = oldTheta;
		Point newP = p0;
		Point lastP = null;

		int index = 0;
		do{
			lastP = newP;
			newP = activePoints.getNextRadial(lastP, angle); //don't pop yet for animation purposes
			angle = lastP.angleTo(newP);

			newTheta = lastP.angleTo(newP);
			System.out.println(newTheta);
			if(newTheta < oldTheta){
				oldTheta -= 2 * Math.PI;
			}
			
		    //first point
			if(lastP == p0) console.println("Sorting points radially and sweeping radially about " + lastP + ".");
			//Not the first point
			else console.println("Found next point at " + lastP + ".  Sorting radially about this point and sweeping radially.");
			
			animateRadialSweep(lastP, oldTheta, newTheta);

//			console.println("Found next point at " + newP + ".  );

			complexity.putData("Radial Sort", new int[]{activePoints.size()});
			activePoints.remove(newP); //pop the next radial point (we do it now so the sweep animates properly).
			convexHull.add(newP); //and add it to the hull list
			
			relinquishControl();
			
			oldTheta = newTheta;
			
			index++;
		} while (newP != p0);
		
		//Pop last point (it is the original point)
		convexHull.remove(convexHull.size() - 1);
		
		console.println("Found original point " + lastP + ", Jarvis march is complete!");

		sets.numbering = sets.fading = false;
		
		finish();
	}
}