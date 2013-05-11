package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;

import algorithm.CHAlgorithm;
import algorithm.CHAlgorithmController;
import algorithm.RealTimeCHAlgorithmController;

public class Main extends javax.swing.JApplet{
	private static final long serialVersionUID = 0x0000000000L;

	public GUIHub gui;
	
	CHAlgorithmController algorithmController;
	
	PointSetCollection pointSets;
	
	public void init(){
		
//		activePoints = PointSet.randomPointSet(Color.BLACK, 100, 200, 200);
//		degeneratePoints = activePoints.removeDegeneratePoints();
		
		pointSets = new PointSetCollection();
		gui = new GUIHub(this, pointSets);
		add(gui);

		pointSets.maxX = gui.canvas.width;
		pointSets.maxY = gui.canvas.height;
	}
	
	public void runAlgorithm(CHAlgorithm algorithm){
		//deactivate the gui
		gui.deactivateGUI();
		
		//launch an algorithm controller in a new thread.  The thread upon completion reactivates the gui.
		algorithmController = new RealTimeCHAlgorithmController(this, algorithm);
		new Thread(algorithmController).start(); 
		
	}
	
	public void stopAlgorithm(){
		algorithmController.terminate();
		gui.activateGUI();
	}
}
