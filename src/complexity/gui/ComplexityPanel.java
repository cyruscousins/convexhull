package complexity.gui;

import gui.GUIConstants;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import complexity.ComplexityAnalysis;

public class ComplexityPanel extends JPanel implements ActionListener{
	
	JScrollPane scrollPane;
	JTextArea text;
	
	ComplexityAnalysis complexity;
	
	JPanel configPanel;
	
	public static final int SORT = 0, CLEAR = 1, REBALANCE = 2;
	String[] buttonStrings = new String[]{"Sort", "Clear", "Rebalance"};
	
	JButton[] buttons;
	
	ButtonGroup radioButtonGroup;
	JRadioButton[] radioButtons;
	
	boolean condensedViewOff = true;
	
	public ComplexityPanel(ComplexityAnalysis complexity){
		this.complexity = complexity;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		setLayout(new GridLayout(1, 8));
		
		//Set up the scroll pane

		text = new JTextArea();
		text.setEditable(false);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(text);
		
		scrollPane.setMinimumSize(new Dimension(100, 100));
		scrollPane.setMaximumSize(new Dimension(1000, 500));
		
		add(scrollPane);
		
		//Set up the buttons
		
		configPanel = new JPanel();
		
		int configWidth = 120;
		configPanel.setMaximumSize(new Dimension(configWidth, 200));
		configPanel.setMinimumSize(new Dimension(configWidth, 100));

		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		
		buttons = new JButton[buttonStrings.length];
		for(int i = 0; i < buttonStrings.length; i++){
			JButton button = new JButton(buttonStrings[i]);
			button.setFont(GUIConstants.BUTTON_FONT);
			button.addActionListener(this);
			
			Dimension size = new Dimension(configWidth, GUIConstants.BUTTON_HEIGHT);
			button.setMinimumSize(size);
			button.setMaximumSize(size);
			
			configPanel.add(button);
			buttons[i] = button;
		}
		
		JLabel label = new JLabel("View Mode:");
		label.setFont(GUIConstants.BUTTON_FONT);
		configPanel.add(label);
		radioButtonGroup = new ButtonGroup();
		radioButtons = new JRadioButton[2];
		
		JPanel radioPanel = new JPanel();
		configPanel.add(radioPanel);
		
		for(int i = 0; i < 2; i++){
			radioButtons[i] = new JRadioButton();
			radioButtons[i].addActionListener(this);
			radioPanel.add(radioButtons[i]);
			radioButtonGroup.add(radioButtons[i]);
		}
		radioButtons[0].setSelected(true);
		
		add(configPanel);

	}
	
	public void updatePanel(){
		String[][] rawData = complexity.detailedInfoAll();
		
		String processed = "";
		
		if(condensedViewOff){ //Everything
			for(int i = 0; i < rawData.length; i++){
				for(int j = 0; j < rawData[i].length; j++){
					if(j > 0) processed += "\t";
					processed += rawData[i][j] + "\n";
				}
			}
		}
		else{ //Just the first line of raw data (a summary).
			for(int i = 0; i < rawData.length; i++){
				processed += rawData[i][0] + "\n";
			}
		}
		
		text.setText(processed);
	}
	
	public void clearAll(){
		text.setText("");
		complexity.reset();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton){
			JButton source = (JButton)e.getSource();
			if(source == buttons[SORT]){
				complexity.sort();
			}
			else if(source == buttons[CLEAR]){
				clearAll();
			}
			else if(source == buttons[REBALANCE]){
				
			}
		}else if(e.getSource() instanceof JRadioButton){
			JRadioButton source = (JRadioButton) e.getSource();
			if(source == radioButtons[0]){
				condensedViewOff = true;
			}
			else if(source == radioButtons[1]){
				condensedViewOff = false;
			}
		}
		//On any action, update the panel text.
		updatePanel();
	}
}
