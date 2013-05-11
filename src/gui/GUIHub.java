package gui;

import java.awt.Dimension;

import geom.Point;
import geom.PointSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import complexity.ComplexityAnalysis;
import complexity.gui.ComplexityPanel;

public class GUIHub extends JPanel{
	public Main main;
	
	public PointSetCollection pointSets;
	
	public OptionPanel options;
	public PointCanvas canvas;
	
	public GUIConsole console;
	public ComplexityAnalysis complexity;
	public GUIConsole complexityLog;
	
	public AnalysisPanel analysisPanel;
	
	//Used by the options panel, set by the canvas.
	public PointSet lastSelectedPointSet;
	public Point lastSelectedPoint;
	
	public GUIHub(Main main, PointSetCollection pointSets){
		this.main = main;
		this.pointSets = pointSets;
		
		options = new OptionPanel(this);
		
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		
		//Analysis panel.  Contains console and complexity analyzer
		console = new GUIConsole();
		complexity = new ComplexityAnalysis();
		complexityLog = new GUIConsole();
		ComplexityPanel complexityPanel = new ComplexityPanel(complexity);
		
		analysisPanel = new AnalysisPanel(console, complexityPanel, complexityLog);
		
		canvas = new PointCanvas(400, 400, this);

//		JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, canvas, console);
//		leftSplit.setResizeWeight(0.75); //favor the top (canvas)
//		leftSplit.setPreferredSize(new Dimension(2000, 2000));

		leftPane.add(canvas);
		leftPane.add(analysisPanel);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

//		add(leftSplit);
		add(leftPane);
		add(options);
		
//		JSplitPane horizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, options);
//		add(horizSplit);
		
		activateGUI();
	}

	boolean guiActive; //Turn buttons and the canvas off.
	
	public void activateGUI(){ //TODO turn on listeners?
		guiActive = true;
	}
	public void deactivateGUI(){ //Turn off listeners?
		guiActive = false;
	}
	
}
