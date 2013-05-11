package gui;

import geom.PointSet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.List;

import auxUtil.ColorManager;

public class PointSetCollection {
	//A group of pointsets, including the active set
	//and degenerate pointsets.  Manages their rendering
	//options as well.

	public List<PointSet> activePoints;
	
	public static final int DEGEN_DUPLICATE = 0, DEGEN_TRILINE = 1, DEGEN_XLINE = 2;
	public List<PointSet> degeneratePoints;
	
	public List<PointSet> convexHull;
	
	public List<PointSet> pairedPoints;
	public PointSet sweeper;
	
	public boolean fading;
	public boolean numbering;
	
	//Some algorithms require this hack.
	public boolean renderUpsideDown;
	
	public boolean convexHullComplete = false; //When this is true, convex hulls are drawn as circular chains (the first and last are joined).  When false, they are not joined.
	int maxX;

	public int maxY;
	
	public PointSetCollection(){
		activePoints = new ArrayList<PointSet>();
		activePoints.add(new PointSet(Color.BLACK));
		
		degeneratePoints = new ArrayList<PointSet>();
		convexHull = new ArrayList<PointSet>();
		pairedPoints = new ArrayList<PointSet>();
	}
	
	//Degeneracy related functions
	public void findDegeneratePoints(){
		for(PointSet active : activePoints){
			List<PointSet> newDegenerates = active.removeDegeneratePoints();
			for(int j = 0; j < newDegenerates.size(); j++){
				if(j < degeneratePoints.size()){
					degeneratePoints.get(j).addAll(newDegenerates.get(j));
				}
				else{
					degeneratePoints.add(newDegenerates.get(j));
				}
			}
		}
	}
	public void addDegeneratePointsToActivePoints(){
		if(degeneratePoints != null){
			for(PointSet p : degeneratePoints){
				activePoints.get(0).addAll(p);
			}
		}
		degeneratePoints.clear();
	}
	public void clearDegeneratePoints(){
		degeneratePoints.clear();
	}
	public int getDegeneratePointCount(){
		int count = 0;
		for(PointSet p : degeneratePoints){
			count += p.size();
		}
		return count;
	}
	
	//Convex Hull related points
	public void addConvexHullPointsToActivePoints(){
		for(PointSet p : convexHull){
			if(p.size() == 0) continue;
			//convex hulls contain a duplicate point at the end, cut it off.
			if(p.get(0).equals(p.get(p.size() - 1))) p.remove(p.size() - 1);
			activePoints.get(0).addAll(p);
			
		}
		convexHull.clear();
	}
	public void clearConvexHullPoints(){
		convexHull.clear();
	}
	
	//Active points functions
	public void consolidateActivePoints() {
		if(activePoints.size() == 0){
			activePoints.add(new PointSet(ColorManager.active));
			return;
		}
		PointSet p0 = activePoints.get(0);
		while(activePoints.size() > 1){
			p0.addAll(activePoints.remove(1));
		}
	}
	
	public void clearAllPoints(){
		degeneratePoints.clear();
		convexHull.clear();
		
		PointSet p = activePoints.get(0);
		p.clear();
		
		activePoints.clear();
		
		activePoints.add(p);
	}
	
	public void flipVertLogic(){
		renderUpsideDown = !renderUpsideDown;
		
		PointSet temp = new PointSet(); //Points can be held by multiple sets.  We do this to stop them from being counted an even number of times.
		for(PointSet p : activePoints){
			temp.addAll(p);
		}
		for(PointSet p : degeneratePoints){
			temp.addAll(p);
		}
		for(PointSet p : convexHull){
			temp.addAll(p);
		}
		for(PointSet p : pairedPoints){
			temp.addAll(p);
		}
		
		temp.removeMemoryDuplicates();

		temp.mirrorY(maxY);
	}
	
	public void flipVertLogic(PointSet allPointsNoDups){
		renderUpsideDown = !renderUpsideDown;
		allPointsNoDups.mirrorY(maxY);
	}
	
	//If logic is flipped, this operation undoes the flip.
	public void vertFlipOff(){
		if(renderUpsideDown) flipVertLogic();
	}

	public void render(Graphics g){
		//TODO STROKE FONT EFFECTS
		
		if(g instanceof Graphics2D){
			Graphics2D g2D = (Graphics2D)g;
			
			g2D.setStroke(new BasicStroke(1.5f));
		}
		
		
		
		
		for(PointSet p : activePoints){
//			p.render(g, this);
			p.advancedRender(g, this, fading, numbering, PointSet.LINEMODE_NONE);
		}
		
		for(PointSet p : degeneratePoints){
			p.render(g, this);
		}
		
		for(PointSet p : convexHull){
			int lineMode;
			if(convexHullComplete){
				lineMode = PointSet.LINEMODE_CIRCULAR;
			}
			else{
				lineMode = PointSet.LINEMODE_LINEAR;
			}
			p.advancedRender(g, this, true, true, lineMode);
		}
		
		for(PointSet p : pairedPoints){
			p.renderPairedLines(g, this);
		}
		
		if(sweeper != null){
			sweeper.advancedRender(g, this, false, false, PointSet.LINEMODE_LINEAR);
		}
	}
}
