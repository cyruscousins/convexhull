package geom;

import gui.PointSetCollection;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import auxUtil.ColorManager;

public class PointSet extends ArrayList<Point>{
	Random random = new Random();
	public Color color;
	
	public PointSet(){
	
	}

	public PointSet(Color color){
		this.color = color;
	}
	
	public PointSet(PointSet copy){
		super(copy);
		this.color = copy.color;
	}
	
	//Pointset stuff that is helpful.
	//Returns a closest point to p
	public Point findClosestPoint(Point p1){
		Point min = null;
		double distance = Double.MAX_VALUE;
		for(Point p2 : this){
			double dS = p1.distance(p2);
			if(dS < distance){
				distance = dS;
				min = p2;
			}
		}
		return min;
	}

	//The classic O(n^2) Cousins duplicate removal algorithm.
	public PointSet removeDuplicates(){
		PointSet dups = new PointSet();
		for(int i = 0; i < size(); i++){
			Point p1 = get(i);
			for(int j = i + 1; j < size(); j++){
				Point p2 = get(j);
				if(p1.equals(p2)){
					dups.add(remove(j));
					j--;
				}
			}
		}
		return dups;
	}

	//Removes duplicates that share memory (removes 2 pointers to the same thing, not 2 pointers to identical things in different locations)
	public PointSet removeMemoryDuplicates(){
		PointSet dups = new PointSet();
		for(int i = 0; i < size(); i++){
			Point p1 = get(i);
			for(int j = i + 1; j < size(); j++){
				Point p2 = get(j);
				if(p1 == p2){
					dups.add(remove(j));
					j--;
				}
			}
		}
		return dups;
	}
	
	public List<PointSet> removeDegeneratePoints(){
		List<PointSet> degenerates = new ArrayList<PointSet>();
		
		PointSet duplicates = removeDuplicates();
		duplicates.color = ColorManager.degen_duplicate;
		degenerates.add(duplicates);

		//Remove any 3+ points on the same line
		
		PointSet lines = new PointSet(ColorManager.degen_triline);
		for(int i = 0; i < size() - 1; i++){
			Point p1 = get(i);
			for(int j = i + 1; j < size(); j++){
				Point p2 = get(j);
				double s1 = p1.angleTo(p2);
				for(int k = j + 1; k < size(); k++){
					Point p3 = get(k);
					
					double s2 = p1.angleTo(p3);
					
					if(Math.abs(s1 - s2) < .0001){
						if(!lines.contains(p1)) lines.add(p1);
						if(!lines.contains(p2)) lines.add(p2);
						if(!lines.contains(p3)) lines.add(p3);
					}
				}
			}
		}
		removeAll(lines);
		degenerates.add(lines);
		
		PointSet xlines = new PointSet(ColorManager.degen_xline);
		
		//Remove all points with non-unique x coordinates.
		for(int i = 0; i < size() - 1; i++){
			boolean foundMatches = false;
			Point p1 = get(i);
			for(int j = i + 1; j < size(); j++){
				if(get(j).x == p1.x){
					foundMatches = true;
					xlines.add(remove(j));
					j--;
				}
			}
			if(foundMatches){
				xlines.add(remove(i));
				i--;
			}
		}
		
		degenerates.add(xlines);
		
		return degenerates;
	}
	
	//return a min x point
	public Point getMinX(){
		int index = 0;
		int min = get(0).x;
		for(int i = 1; i < size(); i++){
			int nMin = get(i).x;
			if(nMin < min){
				min = nMin;
				index = i;
			}
		}
		return get(index);
	}

	//return a max x point
	public Point getMaxX(){
		int index = 0;
		int max = get(0).x;
		for(int i = 1; i < size(); i++){
			int nMax = get(i).x;
			if(nMax > max){
				max = nMax;
				index = i;
			}
		}
		return get(index);
	}

	public void sortX(){
		for(int i = 1; i < size(); i++){
			Point pt = remove(i);
			int x = pt.x;
			int index = 0;
			for(int j = i - 1; j >= 0; j--){
				if(get(j).x < x){
					index = j + 1;
					break;
				}
			}
			add(index, pt);
		}
	}

	public void sortY(){
		for(int i = 1; i < size(); i++){
			Point pt = remove(i);
			int y = pt.y;
			int index = 0;
			for(int j = i - 1; j >= 0; j--){
				if(get(j).y < y){
					index = j + 1;
					break;
				}
			}
			add(index, pt);
		}
	}

	//Sort counterclockwise, starting with angle
	public Point sortRadial(Point p, double sortAngle){
		Point p0 = null;
		for(int i = 0; i < size(); i++){
			if(p.equals(get(i))){
			p0 = remove(i);
				break;
			}
		}
		for(int i = 1; i < size(); i++){
			Point p1 = remove(i);
			double a1 = Math.atan2(p1.y - p.y, p1.x - p.x);
			a1 -= sortAngle;
			while (a1 < 0){
				a1 += Math.PI * 2;
			}
			
			int index = 0;
			for(int j = i - 1; j >= 0; j--){
				Point p2 = get(j);
				double a2 = Math.atan2(p2.y - p.y, p2.x - p.x);
				a2 -= sortAngle;
				while (a2 < 0){
					a2 += Math.PI * 2;
				}
				
				if(a2 < a1){
					index = j + 1;
					break;
				}
			}
			add(index, p1);
		}
		
		return p0;
	}
	
	//Given a point, find the next point counterclockwise from the provided angle.  Assumes provided point does not exist in the pointset.
	public Point getNextRadial(Point p0, double sortAngle){
		double bestAngle = Math.PI * 2;
		Point bestPoint = null;
		for(Point p1 : this){
			double angle = p0.angleTo(p1);
			angle -= sortAngle;
			while (angle < 0){
				angle += Math.PI * 2;
			}
			if(angle < bestAngle){
				bestPoint = p1;
				bestAngle = angle;
			}
		}
		return bestPoint;
	}

	//This implementation is O(n^2), although it could obviously be done in O(n).
	public Point popNextRadial(Point p0, double angle){
		Point p = getNextRadial(p0, angle);
		remove(p);
		return p;
	}
	
	public PointSet sortRadial(){
		//TODO write this.
		return null;
	}
	
	//Split the pointset about a line.  Does not preserve order
	public PointSet splitAbout(Point p0, double angle){
		double fX = Math.sin(angle);
		double fY = Math.cos(angle);
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(fX * (p.x - p0.x) + fY * (p.y - p0.y) < 0){
				newSet.add(remove(i));
				--i;
			}
		}
		return newSet;
	}
	public PointSet splitAbout(Point p0, double dx, double dy){
		double fX = -dy;
		double fY = dx;
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(fX * (p.x - p0.x) + fY * (p.y - p0.y) > 0){
				newSet.add(remove(i));
				--i;
			}
		}
		return newSet;
	}
	
	//Split the pointset between two points, putting the two points into both pointsets
	public PointSet splitBetween(Point p0, Point p1){
		PointSet newSet = splitAbout(p0, p0.x - p1.x, p0.y - p1.y);
		
		if(!newSet.contains(p0)){
			newSet.add(p0);
		}
		if(!newSet.contains(p1)){
			newSet.add(p1);
		}
		
		if(!contains(p0)){
			add(p0);
		}
		if(!contains(p1)){
			add(p1);
		}
		
		return newSet;
		
	}
	
	//Returns a pointset of all points in this set to the right of the argument.  Removes these points
	//from this set.  
	public PointSet removeRightPts(double x){
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(p.x > x){
				newSet.add(removeSwapBack(i));
				--i;
			}
		}
		return newSet;
	}

	public PointSet removeLeftPts(double x){
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(p.x < x){
				newSet.add(removeSwapBack(i));
				--i;
			}
		}
		return newSet;
	}
	
	//Similar to splitHorizLocal, but does not touch the original data.
	public PointSet copyLeftPts(double x){
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(p.x < x){
				newSet.add(get(i));
			}
		}
		return newSet;
	}
	
	//Similar to splitHorizLocal, but does not touch the original data.
	public PointSet copyRightPts(double x){
		PointSet newSet = new PointSet(color);
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			if(p.x > x){
				newSet.add(get(i));
			}
		}
		return newSet;
	}

	//Returns new list containing all points after and including given index
	public PointSet copyAfterIndex(int i){
		PointSet np = new PointSet();
		while(i < size()){
			np.add(get(i));
			i++;
		}
		return np;
	}

	//Retuns new list containing all points before index i.
	public PointSet copyBeforeIndex(int i){
		PointSet np = new PointSet();
		while(i > 0){
			i--;
			np.add(get(i));
		}
		return np;
	}

	//Sweep a line of given slope from above.  
	//Returns the index of the first point met.  Assumes that only one point will be met.
	public int sweepSlopeTop(double slope){
		int topIndex = 0;
		double topVal = Float.MIN_VALUE;
		
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			double val = p.y - slope * p.x;
			if(val > topVal){
				topVal = val;
				topIndex = i;
			}
		}
		return topIndex;
	}
	
	public int minimizeSlopeSweep(double slope){
		int botIndex = 0;
		double botVal = Float.MAX_VALUE;
		
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			double val = p.y - slope * p.x;
			if(val < botVal){
				botVal = val;
				botIndex = i;
			}
		}
		return botIndex;
	}
	
	public Point getMaxSlopeFrom(Point p0){
		Point p = null;
		double max = Double.MIN_VALUE;
		for(Point p1 : this){
			double tMax = p0.slopeTo(p1);
			if(tMax > max){
				max = tMax;
				p = p1;
			}
		}
		return p;
	}
	
	public Point getMinSlopeFrom(Point p0){
		Point p = null;
		double min = Double.MAX_VALUE;
		for(Point p1 : this){
			double tMin = p0.slopeTo(p1);
			if(tMin < min){
				min = tMin;
				p = p1;
			}
		}
		return p;
	}
	
	public void mirrorY(int abtLine){
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			p.y = abtLine - p.y;
		}
	}
	
	/*
	 * RENDERING CODE
	 */
	
	public void renderPoint(Graphics g, PointSetCollection ps, int index, boolean renderNumber){
		Point p = get(index);
		int y = p.y;
		if(ps.renderUpsideDown){
			y = ps.maxY - y;
		}
		g.drawOval(p.x - 2, y - 2, 4, 4);
		
		if(renderNumber){
			g.drawString("" + index, p.x, y + 12);
		}
	}
	
	public void renderLine(Graphics g, PointSetCollection ps, Point p0, Point p1){
		int y0 = p0.y;
		int y1 = p1.y;
		if(ps.renderUpsideDown){
			y0 = ps.maxY - y0;
			y1 = ps.maxY - y1;
		}
		g.drawLine(p0.x, y0, p1.x, y1);
	}

	public void render(Graphics g, PointSetCollection ps){
		int l = size();
		g.setColor(color);
		for(int i = 0; i < l; i++){
			Point p = get(i);
			renderPoint(g, ps, i, false);
		}
	}
	
	public void advancedRender(Graphics g, PointSetCollection ps, boolean fade, boolean number, boolean drawLines){
		int l = size();
		int r0 = color.getGreen(), g0 = color.getGreen(), b0 = color.getBlue();
		
		int r1, g1, b1;
		r1 = g1 = b1 = 255 / 2;
		
		float frac = 1f / l;
		
		//TODO best to set font
		
		g.setColor(color);
		for(int i = 0; i < l; i++){
			if(fade){
				float f = i * frac;
				g.setColor(new Color((int)(f * r1 + (1 - f) * r0), (int)(f * g1 + (1 - f) * g0), (int)(f * b1 + (1 - f) * b0)));
			}
			
			renderPoint(g, ps, i, number);
			
			if(drawLines){
				Point nextP;
				if(i + 1 == size()){
					nextP = get(0);
				}
				else{
					nextP = get(i + 1);
				}
				renderLine(g, ps, get(i), nextP);
			}
		}
	}
	
	public void renderPairedLines(Graphics g, PointSetCollection ps){
		g.setColor(color);
		for(int i = 1; i < size(); i+=2){
			renderLine(g, ps, get(i - 1), get(i));
		}
	}
	
	/*
	 * BASIC SET OPERATIONS
	 */

	public void remove(Point p1){
		for(int i = 0; i < size(); i++){
			if(get(i).equals(p1)){
				remove(i);
				i--;
			}
		}
	}
	
	public Point removeSwapBack(int index){
		int sm1 = size() - 1;
		if(index == sm1){
			return remove(sm1);
		}
		Point p = get(index);
		set(index, remove(sm1));
		return p;
	}
	
	public boolean contains(Point p){
		for(int i = 0; i < size(); i++){
			if(get(i).equals(p)) return true;
		}
		return false;
	}
	
	public void addPair(Point p0, Point p1){
		add(p0);
		add(p1);
	}
	
	//shuffles the pointset quite thoroughly.
	public void shuffle(){
		int s = size();
		for(int i = 0; i < s; i++){
			int newPos = random.nextInt(s);
			Point temp = get(newPos);
			set(newPos, get(i));
			set(i, temp);
		}
	}
	
	public void reverse(){
		int maxItem = size() - 1;
		int mid = size() / 2;
		for(int i = 0; i < mid; i++){
			Point temp = get(i);
			set(i, get(maxItem - i));
			set(maxItem - i, temp);
		}
	}
	
	//returns max if they are unique, else an x where a duplicate occurs.
	public int checkUniqueX(){
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			for(int j = i + 1; j < size(); j++){
				if(p.x == get(j).x){
					return p.x;
				}
			}
		}
		return Integer.MAX_VALUE;
	}
	
	public static PointSet randomPointSet(Color col, int count, int x, int y){
		PointSet p = new PointSet(col);
		for(int i = 0; i < count; i++){
			p.add(new Point(p.random.nextInt(x), p.random.nextInt(y)));
		}
		return p;
	}
	
	//Attempts to find a redundant point.
	public int findRedundantPoint(){
		for(int i = 0; i < size(); i++){
			Point p = get(i);
			for(int j = i + 1; j < size(); j++){
				if(get(j).equals(p)) return i;
			}
		}
		return -1;
	}
}
