package algorithm;

import geom.Point;
import geom.PointSet;
import gui.GUIConsole;
import gui.PointCanvas;
import gui.PointSetCollection;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;

import complexity.ComplexityAnalysis;
import complexity.ComplexityOperation;

import auxUtil.ColorManager;

public class UltimateCH extends CHAlgorithm{
	
	boolean onUpperHull = true;
	
	PointSet allPoints;
	
	PointSet temp0 = new PointSet();
	PointSet temp1 = new PointSet();
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	public UltimateCH(GUIConsole console, ComplexityAnalysis complexity, PointCanvas canvas, PointSetCollection sets, int sleepTime, boolean animate){
		super (console, complexity, canvas, sets, sleepTime, animate);
	}

	public void prepareComplexityAnalyzer() {
		complexity.putOperationType("Find extreme point", ComplexityOperation.linear("n"), .25);
		complexity.putOperationType("Split about line", ComplexityOperation.linear("n"), .75);
		complexity.putOperationType("Calculate median x coordinate", ComplexityOperation.nlogn("n"), 3);
		complexity.putOperationType("Calculate median slope", ComplexityOperation.nlogn("n"), 5);
		complexity.putOperationType("Sweep line through pointset", ComplexityOperation.linear("n"), .3);
	}
	
	Color getColor(int index){
		int intensity = (int)(255 / (1 + Math.sqrt(index / 3) * .25));
		int r = 0;
		int g = 0;
		int b = 0;
		switch (index % 6){
			case 0:
				r = 1;
				break;
			case 1:
				r = g = 1;
				break;
			case 2:
				g = 1;
				break;
			case 3:
				g = b = 1;
				break;
			case 4:
				b = 1;
				break;
			case 5:
				b = r = 1;
				break;
		}
		return new Color(r * intensity, g * intensity, b * intensity);
	}
	
	int curIndex = 0;
	Color getNextColor(){
		return getColor(curIndex++);
	}
	
	public void run(){
		
		if(activePoints.size() <= 3){
			finish();
			return;
		}
		
		ColorManager.initColorManager();
		
		PointSet active = sets.activePoints.get(0);
		sets.activePoints.clear();
		
		allPoints = new PointSet(active);
		
		//TODO show/hide console, clear console.
		console.println("Running the \"Kirkpatrick Siedell Ultimate Planar Convex Hull Algorithm\" on a " + activePoints.size() + " point pointset.");

		complexity.putData("Find extreme point", new int[]{allPoints.size()});
		complexity.putData("Find extreme point", new int[]{allPoints.size()});
		complexity.putData("Split about line", new int[]{allPoints.size()});
				
		PointSet bottom = active;
		PointSet top = active.splitBetween(active.getMinX(), active.getMaxX());

		sets.activePoints.add(top);
		sets.activePoints.add(bottom);
		
		bottom.color = getNextColor();
		top.color = getNextColor();
		
		console.println("First, the pointset is split along the line segment defined by the leftmost and rightmost points.");
		console.println("The lower set is colored " + ColorManager.nameColor(bottom.color) + " and the upper set is colored " + ColorManager.nameColor(top.color) + ".");
		relinquishControl();
		
		//Find top and bottom hulls
		
		console.println("Now, the upper hull is calculated.");
		sets.activePoints.remove(top); //is re added by hull calculation recursive function
		PointSet topHull = calcTopHull(top);
		
		relinquishControl();
		
		PointSet bottomHull = calcBottomHull(bottom);
		
		relinquishControl();
		
		bottomHull.remove(bottomHull.size() - 1); //last item of bottom hull is the rightmost pt, already in top hull.
		bottomHull.reverse();
		
		topHull.addAll(bottomHull);
		topHull.removeDuplicates(); //remove duplicate first/last point.
		sets.convexHull.remove(bottomHull);
		
		relinquishControl();
		
		//TODO global convex hull color, other coloring tricksies.
		
		allPoints.removeAll(topHull);
		sets.activePoints.clear();
		sets.activePoints.add(allPoints);
		
		relinquishControl();
		
		finish();
	}

	public PointSet calcTopHull(PointSet points){
		PointSet topHull = new PointSet(Color.GRAY);
		sets.convexHull.add(topHull);
		
		calcTopHullRecurse(topHull, points);

		addToTopHull(topHull, new Point[]{points.getMinX(), points.getMaxX()});
		
		relinquishControl();
		
		return topHull;
	}
	
	public void calcTopHullRecurse(PointSet topHull, PointSet points){
		
		Point minPt = points.getMinX();
		Point maxPt = points.getMaxX();
		
		int min = minPt.x;
		int max = maxPt.x;
		
		//Split about median (we cheat here, and use a sort to find the median.  In the Real World, this can be done in linear time).
		complexity.putData("Calculate median x coordinate", new int[]{points.size()});
		
		points.sortX();
		
		int splitIndex = points.size() / 2;
		double splitLine = (points.get(splitIndex).x + points.get(splitIndex - 1).x) / 2.0;
		
		PointSet rightSplit = points.copyAfterIndex(splitIndex);
		PointSet leftSplit = points.copyBeforeIndex(splitIndex);
		
		leftSplit.color = getNextColor(); //TODO red and blue? (or 2 set colors)
		rightSplit.color = getNextColor();

		//GRAPHICS STUFF
		sets.activePoints.add(points);
		console.println("Calculating the top hull of the region between " + min + " and " + max + ".  This region is colored " + ColorManager.nameColor(points.color));
		relinquishControl();

		sets.activePoints.remove(points);
		
		console.println("The region between " + min + " and " + max + " is split roughly in half about the line x = " + splitLine + ".");
		console.println("The left side is colored " + ColorManager.nameColor(leftSplit.color) + ", and the right side is colored " + ColorManager.nameColor(rightSplit.color));

		PointSet workingLeft = new PointSet(leftSplit);
		PointSet workingRight = new PointSet(rightSplit);
		
		sets.activePoints.add(workingLeft);
		sets.activePoints.add(workingRight);
		
		relinquishControl();
		//END GRAPHICS
		
		Point[] np = findTopBridge(workingLeft, workingRight);
		addToTopHull(topHull, np);

		sets.activePoints.remove(workingLeft);
		sets.activePoints.remove(workingRight);
		
		if(np[0] != minPt){ //need to do a split on the left
			
			sets.activePoints.add(rightSplit); //graphics
			
			PointSet left = leftSplit.removeLeftPts(np[0].x);
			if(left.size() > 1) //if the size is one, all that remains is the leftmost point.
			{
				left.add(np[0]); //add the left part of the bridge that was just calculated
				calcTopHullRecurse(topHull, left);
			}
			
			sets.activePoints.remove(rightSplit); //graphics
		}
		if(np[1] != maxPt){ //need to do a further split on the right
			
			sets.activePoints.add(leftSplit); //graphics
			
			PointSet right = rightSplit.removeRightPts(np[1].x);
			if(right.size() > 1){
				right.add(np[1]);
				calcTopHullRecurse(topHull, right);
			}

			sets.activePoints.remove(leftSplit); //graphics
		}
	}
	
	//This function calculates the bottom hull.  It operates by flipping everything in logic, and
	//pretty much doing everything the same way as calcTopHull()
	public PointSet calcBottomHull(PointSet points){

		//Mirror about x axis
		sets.flipVertLogic(allPoints);
		
		onUpperHull = false;
		Color temp = bridgeRenderSets[SHALLOW].color;
		bridgeRenderSets[SHALLOW].color = bridgeRenderSets[STEEP].color;
		bridgeRenderSets[STEEP].color = temp;
		
		
		PointSet bottomHull = new PointSet(Color.LIGHT_GRAY);
		sets.convexHull.add(bottomHull);

		sets.activePoints.remove(points); //is re added by the hull calculation recursive function.
		
		calcTopHullRecurse(bottomHull, points);

		addToTopHull(bottomHull, new Point[]{points.getMinX(), points.getMaxX()});

		relinquishControl();
		
		sets.flipVertLogic(allPoints);
		
		return bottomHull;
	}
	
	//Assumes nonempty
	public void addToTopHull(PointSet ch, Point[] pts){
		if (!ch.contains(pts[0])){
			if(ch.size() == 0){
				ch.add(pts[0]);
			}
			else{
				boolean added = false;
				for(int i = 0; i < ch.size(); i++){
					if(pts[0].x < ch.get(i).x){
						ch.add(i, pts[0]);
						added = true;
						break;
					}
				}
				if (!added){
					ch.add(pts[0]);
				}
			}
		}
		if (!ch.contains(pts[1])){
			boolean added = false;
			for(int i = 0; i < ch.size(); i++){
				if(pts[1].x < ch.get(i).x){
					ch.add(i, pts[1]);
					added = true;
					break;
				}
			}
			if (!added){
				ch.add(pts[1]);
			}
		}
	}
	
	//Assumes nonempty
	public void addToBottomHull(PointSet ch, Point[] pts){
		if (!ch.contains(pts[0])){
			if(ch.size() == 0){
				ch.add(pts[0]);
			}
			else{
				boolean added = false;
				for(int i = 0; i < ch.size(); i++){
					if(pts[0].x > ch.get(i).x){
						ch.add(i, pts[0]);
						added = true;
						break;
					}
				}
				if (!added){
					ch.add(pts[0]); //smallest element
				}
			}
		}
		if (!ch.contains(pts[1])){
			boolean added = false;
			for(int i = 0; i < ch.size(); i++){
				if(pts[1].x > ch.get(i).x){
					ch.add(i, pts[1]);
					added = true;
					break;
				}
			}
			if (!added){
				ch.add(pts[1]); //biggest element
			}
		}
	}
	
	//We cheat a little bit here: I didn't implement linear time median finding.  In fact, I implemented median finding
	//about as poorly as one can implement it (O(n^2)).
	
	//Note: No duplicates, or this function (may) fail!  (must have general position).
	//Furthermore, we require an ODD number of points.
	private static int findMedian(double[] data, int size){
		for(int i = 0; i < size; i++){
			int lesses = 0;
			int mores = 0;
			int sames = 0;
			for(int j = 0; j < size; j++){
				if(i == j) continue;
				if(data[i] < data[j]){
					mores++;
				}
				else if(data[i] > data[j])
				{
					lesses++;
				}
				else{
					sames++;
				}
			}
			if(Math.abs(lesses - mores) <= sames){
				return i;
			}
		}
		System.err.println("ERROR: COULD NOT CALC MEDIAN OF:");
		for(int i = 0; i < size; i++){
			System.out.println(data[i]);
		}
		return -1;
	}
	
	private static final int ALL = 0, MEDIAN = 1, STEEP = 2, SHALLOW = 3;
	PointSet[] bridgeRenderSets = new PointSet[]{new PointSet(Color.BLACK), new PointSet(Color.YELLOW), new PointSet(Color.RED), new PointSet(Color.BLUE)}; 

	//Changes order but not contents of provided pointsets
	public Point[] findTopBridge(PointSet set0, PointSet set1){
		Point[] retVal = new Point[2];

		mainBridgeFindLoop: while(true){

			int tempSize = Math.min(set0.size(), set1.size());
			double[] slopes = new double[tempSize];
			
			//TEST CODE: Checks all pairs.

			/*
			if(tempSize <= 2){
				int s0 = set0.size();
				int s1 = set1.size();
				int sc = s0 * s1;
				
				console.println("In left pointset, only " + s0 + " point(s) remain, and in the right pointset, only " + s1 + " points remain.");
				console.println("When < 3 points remain in either pointset, a median slope of random pairings cannot be calculated, so a constant fraction of points cannot be calculated.  However, the total number of possible bridges is equal to the product of elements in the left pointset and the right pointset, which scales linearly.");
				console.println("Finding the bridge by brute force is now a linear operation (" + s0 + " * " + s1 + " = " + sc + " operations).");
				
				//Calculate bridge.

				bridgeFind: for(int l = 0; l < s0; l++){
					Point p0 = set0.get(l);
					for(int r = 0; r < s1; r++){
						Point p1 = set1.get(r);
						
						double slope = p0.slopeTo(p1);
						
						int p0topI = set0.minimizeSlopeSweep(slope);
						int p1topI = set1.minimizeSlopeSweep(slope);
						
						//Check for equality
						if(set0.get(p0topI) == p0 && set1.get(p1topI) == p1){ //found the median line with the sweep.
							retVal[0] = p0;
							retVal[1] = p1;
							break bridgeFind;
						}
					}
				}
				
				//GRAPHICS STUFF:
				//color all combinations
				Color allColor = Color.CYAN;
				PointSet allPairs = new PointSet(allColor);
				for(int l = 0; l < s0; l++){
					for(int r = 0; r < s1; r++){
						allPairs.add(set0.get(l));
						allPairs.add(set1.get(r));
					}
				}
				
				Color bridgeColor = Color.RED;
				PointSet bridge = new PointSet(bridgeColor);
				bridge.add(retVal[0]);
				bridge.add(retVal[1]);
				
				//Show all pairs colored
				console.println("All pairs shown in " + ColorManager.nameColor(allColor) + " (" + sc + " pairs).");
				sets.pairedPoints.add(allPairs);
				relinquishControl();
				
				//Now show colored bridge
				console.println("Bridge found between points " + retVal[0] + " and " + retVal[1] + ", shown in " + ColorManager.nameColor(bridgeColor) + ".");
				sets.pairedPoints.add(bridge);
				
				relinquishControl();
				
				//put state back to normal.

				sets.pairedPoints.remove(allPairs);
				sets.pairedPoints.remove(bridge);
				
				break mainBridgeFindLoop;
			}
			*/
			
			if(tempSize % 2 == 0) tempSize--; //Must be an odd number of points.
			
			//Shuffle the pointsets (equivalent to random picking, but simpler).
			//If we don't do this, the algorithm gets stuck.
			set0.shuffle();
			set1.shuffle();
			
			for(int i = 0; i < tempSize; i++){
				slopes[i] = set0.get(i).slopeTo(set1.get(i));
			}

			//Find the median and sweep it down, to find if we are too steep or not.
			complexity.putData("Calculate median slope", new int[]{tempSize});
			int median = findMedian(slopes, tempSize);
			
			//GRAPHICS: RENDER ALL CONNECTOR POINTS
			for(int i = 0; i < tempSize; i++){
				bridgeRenderSets[ALL].addPair(set0.get(i), set1.get(i));
				if(slopes[i] > slopes[median]) bridgeRenderSets[SHALLOW].addPair(set0.get(i), set1.get(i)); //coordinate system seems flipped.
				else if (slopes[i] < slopes[median]) bridgeRenderSets[STEEP].addPair(set0.get(i), set1.get(i));
				else bridgeRenderSets[MEDIAN].addPair(set0.get(i), set1.get(i));
			}
			
			sets.pairedPoints.add(bridgeRenderSets[ALL]);
			
			console.println("Pairing up left and right pointsets randomly.");
			relinquishControl();
			
			sets.pairedPoints.remove(bridgeRenderSets[ALL]);
			bridgeRenderSets[ALL].clear();
			
			for(int i = 1; i < bridgeRenderSets.length; i++){
				sets.pairedPoints.add(bridgeRenderSets[i]);
			}
			
			console.println("Median slope of pairings shown in yellow.  Steeper slopes are red, shallower are blue.");

			relinquishControl();

			for(int i = 1; i < bridgeRenderSets.length; i++){
				sets.pairedPoints.remove(bridgeRenderSets[i]);
				bridgeRenderSets[i].clear();
			}
			
			//GRAPHICS END

			int p0topI = set0.minimizeSlopeSweep(slopes[median]); //must flip this, coordinate system is upside down.
			int p1topI = set1.minimizeSlopeSweep(slopes[median]);

			complexity.putData("Sweep line through pointset", new int[]{set0.size()});
			complexity.putData("Sweep line through pointset", new int[]{set1.size()});
			
			double mSlope = set0.get(p0topI).slopeTo(set1.get(p1topI));
			
			//Check for equality
			if(p0topI == median && p1topI == median){ //found the median line with the sweep.
				retVal[0] = set0.get(median);
				retVal[1] = set1.get(median);
				console.println("Found the bridge, between points " + retVal[0] + " and " + retVal[1]);
				break;
			}
			
			boolean guessTooSteep = mSlope > slopes[median];
			
			if(guessTooSteep){ //too steep.  Cut off the left end of all steeper guesses
				int ptsRemoved = 0;
				for(int i = tempSize - 1; i >= 0; i--){
					if(slopes[i] < slopes[median]){
						temp0.add(set0.remove(i));
						ptsRemoved++;
					}
				}
				
				String output = "Median guess slope (" + df.format(mSlope) + ") was too " + (onUpperHull ? "steep (+)" : "shallow (-)") + ".  ";
				if(ptsRemoved > 0){
					output += "Removed " + ptsRemoved + " left points of pairs with " + (onUpperHull ? "greater" : "lesser") + " sslope.  ";
				}
				console.println(output);
			}
			else{ //too shallow
				int ptsRemoved = 0;
				for(int i = tempSize - 1; i >= 0; i--){
					if(slopes[i] > slopes[median]){ //If we guess to shallow, delete the right side of shallower guesses.
						temp1.add(set1.remove(i));
						ptsRemoved++;
					}
				}

				String output = "Median guess slope (" + df.format(mSlope) + ") was too " + (onUpperHull ? "shallow (-)" : "steep (+)") + ".  ";
				if(ptsRemoved > 0){
					output += "Removed " + ptsRemoved + " right points of pairs with " + (onUpperHull ? "lesser" : "greater") + " slope.  ";
				}
				console.println(output);
			}

			
			//Rebalance pointsets
			//This operation is equivalent to a linear time median calculation of the 2 sets.
			complexity.putData("Calculate median x coordinate", new int[]{set0.size() + set1.size()});
			
			while(set0.size() > set1.size()){
				Point maxX = set0.getMaxX();
				set0.remove(maxX);
				set1.add(maxX);
			}

			while(set1.size() > set0.size()){
				Point minX = set1.getMinX();
				set1.remove(minX);
				set0.add(minX);
			}
			
			
			console.println("Rebalancing the left and right halfs of the pointset.");
			relinquishControl();
			
			
		}
		
		//Re-add the trash so the original sets are not mutated.
		set0.addAll(temp0);
		temp0.clear();
		
		set1.addAll(temp1);
		temp1.clear();
		
		return retVal;
	}
	
	public void cleanup(){
		super.cleanup();
		sets.vertFlipOff();
	}

}