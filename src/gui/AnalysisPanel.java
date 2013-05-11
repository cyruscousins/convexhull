package gui;

import javax.swing.JTabbedPane;

import complexity.gui.ComplexityPanel;

//This panel lies in the lower left hand side of the screen, and displays analysis information, such as the console or the complexity analysis.
public class AnalysisPanel extends JTabbedPane{
	
	public GUIConsole console;
	public ComplexityPanel complexity;
	
	public AnalysisPanel(GUIConsole console, ComplexityPanel complexity) {
		super();
		this.console = console;
		this.complexity = complexity;
		
		add(console);
		add(complexity);

		setTitleAt(0, "Console");
		setTitleAt(1, "Complexity Analysis Report");
		
		setToolTipTextAt(0, "Open the console tab to see text ouput produced by the program.");
		setToolTipTextAt(1, "Open the complexity analysis report to gain insight into the cost and complexity of various components of the available algorithms.");
	}
	
}
