package gui;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import javax.xml.soap.Text;

public class GUIConsole extends JScrollPane{
	JTextArea text;
	boolean consoleOn = true;
	public GUIConsole(){
		setMinimumSize(new Dimension(400, 100));
		text = new JTextArea();
		text.setEditable(false);
		
		setViewportView(text);
	}
	public void println(String s){
		if(consoleOn){
			text.append(s + "\n");
			text.setCaretPosition(text.getText().length());
			text.repaint();
		}
	}
	public void printerr(String s){
		text.append(s + "\n");
		text.setCaretPosition(text.getText().length());
		text.repaint();
	}
	public void clear(){
		text.setText("");
		text.repaint();
	}
	
}
