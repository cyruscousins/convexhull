package gui;

import geom.Point;
import geom.PointSet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.security.acl.Owner;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class PointCanvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{
	public int width, height;
	public GUIHub parent;
	
	public PointCanvas(int width, int height, GUIHub gui) {
//		this.width = width;
//		this.height = height;
		this.parent = gui;

		Dimension min = new Dimension(width, height);
		Dimension pref = new Dimension(2000, 2000);
		setMinimumSize(min);
		setPreferredSize(pref);

		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
		
		setBorder(new LineBorder(Color.BLACK, 4));
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		parent.pointSets.render(g);
		
		//Draw the location of the mouse.  Use a fancy shadow to make the text readable over points.
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(mouseString, width - 64 - 16, height - 2 + 1);
		g.drawString(mouseString, width - 64 - 16 - 1, height - 2);
		g.setColor(Color.BLACK);
		g.drawString(mouseString, width - 64 - 16, height - 2);
	}
	
	String mouseString = "";
	/*
	 * Mouse listening information.
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(selectedPoint != null){
			selectedPoint = null;
		}
		
		mouseString = "";
		repaint();
	}
	
	Point selectedPoint;

	@Override
	public void mousePressed(MouseEvent e) {
		Point p1 = new Point(e.getX(), e.getY());
		
		PointSet set = parent.pointSets.activePoints.get(0);
		selectedPoint = set.findClosestPoint(p1);
		
		//Check for convex hull selection
		
		for(PointSet p : parent.pointSets.convexHull){
			if(p.size() == 0) continue; //ignore empty sets.
			Point p2 = p.findClosestPoint(p1);
			if(selectedPoint == null || p1.distanceSquared(p2) <= p1.distanceSquared(selectedPoint)){
				selectedPoint = p2;
				parent.lastSelectedPoint = p2;
				set = p;
			}
		}
		
		for(PointSet p : parent.pointSets.degeneratePoints){
			if(p.size() == 0) continue; //ignore empty sets.
			Point p2 = p.findClosestPoint(p1);
			if(selectedPoint == null || p1.distanceSquared(p2) <= p1.distanceSquared(selectedPoint)){ //favor degenerate selection!
				selectedPoint = p2;
				parent.lastSelectedPoint = p2;
				set = p;
			}
		}
		
		if(selectedPoint == null){
			//empty set
			return;
		}
		
		int mDSqr = parent.options.sliders[OptionPanel.SLIDER_SENSITIVITY].getValue();
		mDSqr *= mDSqr;
		if(selectedPoint.distanceSquared(p1) > mDSqr){
			selectedPoint = null; //too far away.
		}
		else{
			parent.lastSelectedPoint = selectedPoint;
			parent.lastSelectedPointSet = set;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if(button == 1){ //left click to create/move
			if(selectedPoint != null){
				selectedPoint.x = e.getX();
				selectedPoint.y = e.getY();
				selectedPoint = null;
				repaint();
			}
			else{
				Point p = new Point(e.getX(), e.getY());
				parent.pointSets.activePoints.get(0).add(p);
				repaint();
			}
		}
		else if(button == 3){ //right click to delete
			if(selectedPoint != null){
				parent.pointSets.activePoints.get(0).remove(selectedPoint);
				if(parent.pointSets.degeneratePoints != null){
					for(PointSet p : parent.pointSets.degeneratePoints){
						p.remove(selectedPoint);
					}
				}
				repaint();
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		width = getWidth();
		height = getHeight();
		
		parent.pointSets.maxX = width;
		parent.pointSets.maxY = height;
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseString = "(" + arg0.getX() + ", " + arg0.getY() + ")";
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseString = "(" + arg0.getX() + ", " + arg0.getY() + ")";
		repaint();
	}
}
