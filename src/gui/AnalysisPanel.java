package gui;

import java.awt.Dimension;

import javax.swing.JTabbedPane;

import complexity.gui.ComplexityPanel;

//This panel lies in the lower left hand side of the screen, and displays analysis information, such as the console or the complexity analysis.
public class AnalysisPanel extends JTabbedPane{
	
	public GUIConsole console;
	public ComplexityPanel complexity;
	public GUIConsole complexityLog;
	
	public AnalysisPanel(GUIConsole console, ComplexityPanel complexity, GUIConsole complexityLog) {
		super();
		setMinimumSize(new Dimension(400, 150));
		setMaximumSize(new Dimension(1000, 200));
		
		this.console = console;
		this.complexity = complexity;
		this.complexityLog = complexityLog;
		
		add(console);
		add(complexity);
		add(complexityLog);

		setTitleAt(0, "Console");
		setTitleAt(1, "Complexity Analysis Report");
		setTitleAt(2, "Complexity Log");
		
		setToolTipTextAt(0, "Open the console tab to see text ouput produced by the program.");
		setToolTipTextAt(1, "Open the complexity analysis report to gain insight into the cost and complexity of various components of the available algorithms.");
		setToolTipTextAt(2, "See a real time report of the complexity and absolute cost of each component operation of each algorithm.");
	}
	
}
