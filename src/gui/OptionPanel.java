package gui;

import geom.Point;
import geom.PointSet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import algorithm.CHAlgorithm;
import algorithm.GrahamScan;
import algorithm.JarvisMarch;
import algorithm.UltimateCH;
import auxUtil.ColorManager;

public class OptionPanel extends JPanel implements ActionListener{
	public static final int WIDTH = 250;
	
	GUIHub parent;
	
	JTextArea ptsTextArea;
	
	boolean animationOn = true;
	
	public static final int 
	
			BUTTON_GENERAL = 0, 
			BUTTON_FIXDEGEN = BUTTON_GENERAL + 1,
			
			BUTTON_ADDDEGENTOACTIVE = BUTTON_FIXDEGEN + 1,
			BUTTON_CLEARDEGEN = BUTTON_ADDDEGENTOACTIVE + 1,
			
			BUTTON_ADDCONVEX = BUTTON_CLEARDEGEN + 1,
			BUTTON_REMOVECONVEX = BUTTON_ADDCONVEX + 1,
			
			BUTTON_RANDOM = BUTTON_REMOVECONVEX + 1, 
			BUTTON_CLEAR = BUTTON_RANDOM + 1, 
			
			BUTTON_REGPOLY = BUTTON_CLEAR + 1, 
			BUTTON_STARPOLY = BUTTON_REGPOLY + 1,
			
			BUTTON_TOTEXT = BUTTON_STARPOLY + 1, 
			BUTTON_FROMTEXT = BUTTON_TOTEXT + 1,
			
			BUTTON_TEXTTOFILE = BUTTON_FROMTEXT + 1, 
			BUTTON_TEXTFROMFILE = BUTTON_TEXTTOFILE + 1,
			
			BUTTON_SORTX = BUTTON_TEXTFROMFILE + 1,
			BUTTON_SORTY = BUTTON_SORTX + 1,
	
			BUTTON_SORTCLOCKWISE = BUTTON_SORTY + 1,
			BUTTON_SORTCOUNTERCLOCKWISE = BUTTON_SORTX + 1;
	
			//TODO video/framecapture modes (single capture, keyframe video, allframe video)
	
			//TODO colorPicker
	
	JButton[] actionButtons;
	
	String[] actionButtonStrings = new String[]{
			"Check Degeneracy", "Fix Degeneracy", 
			"Re-Add Degen Pts", "Remove Degen Pts", 
			"Re-add Convex", "Remove Convex", 
			"Randomize", "Clear", 
			"Form Regular Poly", "Form Star Poly", 
			"To Text", "From Text", 
			"Save CSV", "Read CSV", 
			"Sort X", "Sort Y",
			"Sort Clockwise", "Sort CClockwise"
			//"Recolor Pointset", VIDEO
			};
	
	String[] actionButtonTips = new String[]{
			"Mark all degenerate points.  Degenerate points in a set include nonunique points, points that share a line with at least 2 other points, and points with nonunique x coordinates.", 
			"Fix all degenerate points by moving them slightly.",
			"Unmark degenerate points.", 
			"Remove all points currently marked as degenerate.", 
			"Remove all points currently marked as convex hull points.",
			"Unmark convex hull points.",
			"Replace the current pointset with a random one.  Use the random slider to control the size of the new pointset.", 
			"Remove all points from the current point set.", 
			"Rearrange the current point set to a regular polygon.",
			"Rearrange the current point set to form a star polygon.",
			"Output the current point set as a csv.  Output will be stored in the text box below.",
			"Read in a csv point set from the below text box.",
			"Write the current text in the text area to a CSV file.",
			"Read from a CSV file to the text area.",
			"Sort points with respect to x coordinate.",
			"Sort points with respect to y coordinate.",
			"Sort points clockwise, with respect to the last selected point (select a point by clicking on it).",
			"Sort points counterclockwise, with respect to the last selected point (select a point by clicking on it).",
//			"Recolor the last pointset selected (select a pointset by clicking a member element in the pointset canvas).",
	};


	public JButton[] algorithmButtons;
	public static final int ALGORITHM_JARVIS = 0, ALGORITHM_GRAHAM = 1, ALGORITHM_ULTIMATE = 2;
	public static final String[] algorithmNames = new String[]{"Jarvis March", "Graham Scan", "Ultimate Planar CH"};
	public static final String[] algorithmTips = new String[]{
		"Calculate the convex hull using the Jarvis March algorithm", 
		"Calculate the convex hull using the Graham Scan algorithm",
		"Calculate the convex hull using the Kirkpatrick-Seidel Ultimate Planar Convex Hull algorithm"
	};
	
	//Used to control the flow of the running algorithm
	public JButton[] controlButtons;
	public static final int CONTROL_STOP = 0, CONTROL_PLAY = 1, CONTROL_PAUSE = 2, CONTROL_NEXT = 3, CONTROL_SKIP = 4;
	public static final String[] controlNames = new String[]{"Stop", "Play", "Pause", "Next", "Skip"};
	public static final String[] controlTips = new String[]{
		"Stop the current convex hull algorithm in its tracks (permanantly).",
		"Suspend the currently running convex hull algorithm.",
		"Resume the currently running convex hull algorithm.",
		"Step to the next operation of the currently running convex hull algorithm.",
		"Skip ahead to the completed convex hull."
	};
	
	public static final String ACTION = "Action", ALGORITHM = "Algorithm", CONTROL = "Control"; //Used as identifiers on buttons.
	
	public static final int SLIDER_SENSITIVITY = 0, SLIDER_RANDOM = 1, SLIDER_SPEED = 2;
	String[] sliderNames = new String[]{"Selection sensitivity", "Random count", "Algorithm sleep time"};
	int[] mins = new int[]{1, 1, 1}, maxes = new int[]{16, 255, 64}, values = new int[]{6, 32, 8};
	JSlider[] sliders;
	
	public OptionPanel(GUIHub parent){
		this.parent = parent;
		build();
	}
	
	public void build(){
		Dimension d;
		
		JPanel sliderPanel = new JPanel(new GridLayout(sliderNames.length, 2));
		sliders = new JSlider[sliderNames.length];
		for(int i = 0; i < sliderNames.length; i++){
			sliders[i] = new JSlider(mins[i], maxes[i], values[i]);

			int majorSpacing = (maxes[i] - mins[i] - 1) / 4;
			if(majorSpacing % 2 == 1) majorSpacing--;
			
			sliders[i].setMajorTickSpacing(majorSpacing);
			sliders[i].setMinorTickSpacing(majorSpacing / 2);
			sliders[i].setPaintLabels(true);
			sliders[i].setPaintTicks(true);

			sliders[i].setFont(GUIConstants.BUTTON_FONT);
//			sliders[i].setExtent(2);
//			sliders[i].setMaximumSize(new Dimension(WIDTH / 2, 10));
//			sliders[i].setPreferredSize(new Dimension(WIDTH / 2, 10));
			
			JLabel label = new JLabel(sliderNames[i]);
			label.setFont(GUIConstants.BUTTON_FONT);
			
			sliderPanel.add(label);
			sliderPanel.add(sliders[i]);
			
		}

		d = new Dimension(WIDTH, 150);
		sliderPanel.setMaximumSize(d);
		sliderPanel.setMinimumSize(d);
		
		
		JPanel buttonPanel = new JPanel(new GridLayout(actionButtonStrings.length / 2, 2));
		
		actionButtons = new JButton[actionButtonStrings.length];
		for(int i = 0; i < actionButtonStrings.length; i++){
			actionButtons[i] = new JButton(actionButtonStrings[i]);
			actionButtons[i].setFont(GUIConstants.BUTTON_FONT);
			actionButtons[i].setToolTipText(actionButtonTips[i]);
			actionButtons[i].setActionCommand(ACTION);
			buttonPanel.add(actionButtons[i]);
			actionButtons[i].addActionListener(this);
		}
		
		d = new Dimension(WIDTH, actionButtons.length * GUIConstants.BUTTON_HEIGHT / 2);
		
		buttonPanel.setMaximumSize(d);
		buttonPanel.setMinimumSize(d);

		ptsTextArea = new JTextArea();
		
		ptsTextArea.setRows(32);

		d = new Dimension(WIDTH, 200);

		JScrollPane scroll = new JScrollPane(ptsTextArea);
		
		scroll.setMaximumSize(new Dimension(WIDTH, 1000));
		scroll.setMinimumSize(new Dimension(WIDTH, 20));
		scroll.setPreferredSize(new Dimension(WIDTH, 1000));

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(sliderPanel);
		add(buttonPanel);
		add(scroll);
		
		JLabel algorithmButtonPanelLabel = new JLabel("Convex Hull Algorithms:");
		algorithmButtonPanelLabel.setFont(GUIConstants.BUTTON_FONT);
		
		JPanel algorithmButtonPanel = new JPanel(new GridLayout(2, 3));
		algorithmButtonPanel.add(algorithmButtonPanelLabel);
		algorithmButtons = new JButton[algorithmNames.length];
		
		for(int i = 0; i < algorithmButtons.length; i++){
			algorithmButtons[i] = new JButton(algorithmNames[i]);
			algorithmButtons[i].setFont(GUIConstants.BUTTON_FONT);
			algorithmButtons[i].setToolTipText(algorithmTips[i]);
			algorithmButtons[i].setActionCommand(ALGORITHM);
			algorithmButtonPanel.add(algorithmButtons[i]);
			algorithmButtons[i].addActionListener(this);
		}

		d = new Dimension(WIDTH, algorithmButtons.length * GUIConstants.BUTTON_HEIGHT / 2);
		
		algorithmButtonPanel.setMinimumSize(d);
		algorithmButtonPanel.setMaximumSize(d);
		
		add(algorithmButtonPanel);
		
		JPanel controlButtonPanel = new JPanel(new GridLayout(1, controlNames.length));
		controlButtons = new JButton[controlNames.length];
		
		for(int i = 0; i < controlButtons.length; i++){
			controlButtons[i] = new JButton(controlNames[i]);
			
			controlButtons[i].setFont(GUIConstants.BUTTON_FONT);
			controlButtons[i].setToolTipText(controlTips[i]);
			controlButtons[i].setBorder(new LineBorder(Color.WHITE, 1));
			
			controlButtons[i].setActionCommand(CONTROL);
			controlButtons[i].addActionListener(this);
			controlButtonPanel.add(controlButtons[i]);
		}

		d = new Dimension(WIDTH, GUIConstants.BUTTON_HEIGHT);
		
		controlButtonPanel.setMinimumSize(d);
		controlButtonPanel.setMaximumSize(d);
		
		add(controlButtonPanel);
		
		d = new Dimension(WIDTH, 1200);
//		setMaximumSize(d);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source instanceof JButton){
			if(((JButton)source).getActionCommand() == ACTION){
				
				if(!parent.guiActive){
					return;
				}
				
				if(source == actionButtons[BUTTON_GENERAL]){
					parent.console.println("Checking for degenerate points:");
					parent.pointSets.addDegeneratePointsToActivePoints();
					parent.pointSets.findDegeneratePoints();
					
					boolean foundDegens = false;
					
					//Nonunique points
					PointSet ps = parent.pointSets.degeneratePoints.get(0);
					if(ps.size() != 0){
						parent.console.println("Found " + ps.size() + " nonunique points.");
						parent.console.println("These points have been colored " + ColorManager.nameColor(ps.color));
						foundDegens = true;
					}

					//Lines with > 3 points
				    ps = parent.pointSets.degeneratePoints.get(1);
					if(ps.size() != 0){
						parent.console.println("Found " + ps.size() + " points on lines with > 2 points.");
						parent.console.println("These points have been colored " + ColorManager.nameColor(ps.color));
						foundDegens = true;
					}
					
					//points sharing an x coordinate
				    ps = parent.pointSets.degeneratePoints.get(2);
					if(ps.size() != 0){
						parent.console.println("Found " + ps.size() + " points sharing an x coordinate with another point in the set.");
						parent.console.println("These points have been colored " + ColorManager.nameColor(ps.color));
						foundDegens = true;
					}
					
					if(!foundDegens){
						parent.console.println("No degenerate points found.");
					}
					
				}
				if(source == actionButtons[BUTTON_FIXDEGEN]){
					parent.console.println("Attempting to fix degeneracy by nudging degenerate points.");
					Random rand = new Random();
					parent.pointSets.findDegeneratePoints();
					int triesLeft = 128;
					while(triesLeft > 0){
						if(parent.pointSets.getDegeneratePointCount() == 0){
							parent.console.println("Fix Successful.");
							break;
						}
						for(PointSet ps : parent.pointSets.degeneratePoints){
							for(Point p : ps){
								p.x += rand.nextInt(3) - 1;
								p.y += rand.nextInt(3) - 1;
							}
						}
						parent.pointSets.addDegeneratePointsToActivePoints();
						parent.pointSets.findDegeneratePoints();
						triesLeft--;
					}
					if(parent.pointSets.getDegeneratePointCount() != 0){
						parent.console.println("Fix Unsuccessful (gave up after 128 tries).");
					}
				}
				else if(source == actionButtons[BUTTON_ADDDEGENTOACTIVE]){
					parent.pointSets.addDegeneratePointsToActivePoints();
				}
				else if(source == actionButtons[BUTTON_CLEARDEGEN]){
					parent.pointSets.clearDegeneratePoints();
				}
				else if(source == actionButtons[BUTTON_ADDCONVEX]){
					parent.pointSets.addConvexHullPointsToActivePoints();
				}
				else if(source == actionButtons[BUTTON_REMOVECONVEX]){
					parent.pointSets.convexHull.clear();
				}
				else if(source == actionButtons[BUTTON_REMOVECONVEX]){
					parent.pointSets.convexHull.clear();
				}
				else if(source == actionButtons[BUTTON_RANDOM]){
					parent.pointSets.clearAllPoints();
					parent.pointSets.activePoints.clear();
					parent.pointSets.activePoints.add(PointSet.randomPointSet(Color.BLACK, sliders[SLIDER_RANDOM].getValue(), parent.canvas.width, parent.canvas.height));
				}
				else if(source == actionButtons[BUTTON_CLEAR]){
					parent.pointSets.clearAllPoints();
				}
				else if(source == actionButtons[BUTTON_REGPOLY]){
					PointSet points = parent.pointSets.activePoints.get(0);
					
					int cx = parent.canvas.width / 2;
					int cy = parent.canvas.height / 2;
					
					int radius = Math.min(cx, cy) * 95 / 100;
					
					double tStep = Math.PI * 2 / points.size();
					for(int i = 0; i < points.size(); i++){
						double theta = tStep * i;
						points.get(i).set(cx + (int)(Math.cos(theta) * radius), cy + (int)(Math.sin(theta) * radius));
					}
				}
				else if(source == actionButtons[BUTTON_STARPOLY]){
					PointSet points = parent.pointSets.activePoints.get(0);

					int cx = parent.canvas.width / 2;
					int cy = parent.canvas.height / 2;
					
					int rMax = Math.min(cx, cy);
					
					double tStep = Math.PI * 2 / points.size();
					for(int i = 0; i < points.size(); i++){
						double theta = tStep * i;
						double radius = rMax * Math.random();
						points.get(i).set(cx + (int)(Math.cos(theta) * radius), cy + (int)(Math.sin(theta) * radius));
					}
				}
				else if(source == actionButtons[BUTTON_TOTEXT]){
					String out = "";
					int ptCount = 0;
					for(PointSet ps : parent.pointSets.activePoints){
						for(Point p : ps){
							out += "(" + p.x + " " + p.y + ")";
							if(++ptCount % 5 == 0){
								out += ",\n";
							}
							else out += ", ";
						}
					}
					ptsTextArea.setText(out);
				}
				else if(source == actionButtons[BUTTON_FROMTEXT]){
					String in = ptsTextArea.getText();
					String[] pts = in.split("\\),[ \n\t]*\\(");
					pts[0] = pts[0].replace("(", "");
					pts[pts.length - 1] = pts[pts.length - 1].replaceFirst("\\),[ \n\t]*", "");
					for(String s : pts){
						if(s.equals("")) continue;
						try{
							String[] dat = s.split(" +");
							Point p = new Point(Integer.valueOf(dat[0]), Integer.valueOf(dat[1]));
							//TODO check to see if the point already exists
							parent.pointSets.activePoints.get(0).add(p);
						}
						catch(Exception ex){
							System.out.println(s);
							ex.printStackTrace();
							ptsTextArea.setText(ptsTextArea.getText() + "\nERROR: Invalid Input Text.  Please format your input as comma separated value pairs\nEx: (1 2), (3 4)");
//							System.exit(1); //TODO probably should just throw an error...
						}
					}
				}
				else if(source == actionButtons[BUTTON_SORTX]){
					parent.pointSets.activePoints.get(0).sortX();
				}
				else if(source == actionButtons[BUTTON_SORTY]){
					parent.pointSets.activePoints.get(0).sortY();
				}
				//TODO radial sorting about point entered in text console (possibly in both directions
				//TODO remove full convex hulls
				else if(source == actionButtons[BUTTON_TEXTFROMFILE]){
					JFileChooser fc = new JFileChooser();
					int retVal = fc.showOpenDialog(this);
					if(retVal == JFileChooser.APPROVE_OPTION){
						try{
							File f = fc.getSelectedFile();
							BufferedReader reader = new BufferedReader(new FileReader(f));
							String fileText = "";
							while(reader.ready()){
								fileText += reader.readLine() + "\n";
							}
							reader.close();
							ptsTextArea.setText(fileText);
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
				else if(source == actionButtons[BUTTON_TEXTTOFILE]){
					JFileChooser fc = new JFileChooser();
					int retVal = fc.showOpenDialog(this);
					if(retVal == JFileChooser.APPROVE_OPTION){
						try{
							File f = fc.getSelectedFile();
							FileOutputStream writer = new FileOutputStream(f);
							writer.write(ptsTextArea.getText().getBytes());
							writer.flush();
							writer.close();
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
//				if(source == buttons[BUTTON_RECOLOR]){
//					if(parent.lastSelectedPointSet != null){
//						Color pickedColor = JColorChooser.showDialog(this, "Please select a new color for this pointset.",
//							                                 parent.lastSelectedPointSet.color);
//						if(pickedColor != null) parent.lastSelectedPointSet.color = pickedColor;
//					}
//				}
				
				//SCREENSHOT

				parent.canvas.repaint();
			}
			else if (((JButton)source).getActionCommand() == ALGORITHM){

				if(!parent.guiActive){
					return;
				}
				
				CHAlgorithm algorithm = null;
				if(source == algorithmButtons[ALGORITHM_GRAHAM]){
					algorithm = new GrahamScan(parent.console, parent.complexity, parent.canvas, parent.pointSets, sliders[OptionPanel.SLIDER_SPEED].getValue() * 100, animationOn);
				}
				else if(source == algorithmButtons[ALGORITHM_JARVIS]){
					algorithm = new JarvisMarch(parent.console, parent.complexity, parent.canvas, parent.pointSets, sliders[OptionPanel.SLIDER_SPEED].getValue() * 100, animationOn);
				}
				else if(source == algorithmButtons[ALGORITHM_ULTIMATE]){
					algorithm = new UltimateCH(parent.console, parent.complexity, parent.canvas, parent.pointSets, sliders[OptionPanel.SLIDER_SPEED].getValue() * 100, animationOn);
				}
				
				parent.console.consoleOn = animationOn; //no animation, no step by step prints.
				
				parent.main.runAlgorithm(algorithm);
			}
			else if(((JButton)source).getActionCommand() == CONTROL){
				if(parent.main.algorithmController != null){
					if(source == controlButtons[CONTROL_STOP]){
						parent.main.stopAlgorithm();
					}
					else if(source == controlButtons[CONTROL_PLAY]){
						parent.main.algorithmController.unpause();
					}
					else if(source == controlButtons[CONTROL_PAUSE]){
						parent.main.algorithmController.pause();
						parent.console.println("Algorithm paused.  Press play to resume animation, and press next to advance to the next step of the algorithm.");
					}
					else if(source == controlButtons[CONTROL_NEXT]){
						parent.main.algorithmController.step();
					}
					else if(source == controlButtons[CONTROL_SKIP]){
						parent.console.println("SKIPPING ALGORITHM");
						parent.main.algorithmController.skip();
					}
				}
			}
		}
	}
}
