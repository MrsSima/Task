package sample.handlers;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CreateGUI();
	    
		return null;
	}
	private void CreateGUI() {
		JMenuBar menuBar = new JMenuBar();
		JMenu openM = new JMenu("Open file");
		JMenu helpM = new JMenu("Help");
		menuBar.add(openM);
		menuBar.add(helpM);
		
	    
	    
	    JPanel upperP = new JPanel(new GridLayout(0, 1));
	    JLabel titleL = new JLabel("Title:");
	    upperP.add(titleL, BorderLayout.NORTH);
	    JTextField titleTF = new JTextField(30);
	    upperP.add(titleTF, BorderLayout.SOUTH);
	    JLabel memoryL = new JLabel("Memory:");
	    upperP.add(memoryL, BorderLayout.LINE_START);
	    
	    //TODO make NORMAL formating of label and textfields
	    JPanel midP = new JPanel(new GridLayout(0, 3));   
	    JTextField nameTF = new JTextField();
	    JTextField originTF = new JTextField();
	    JTextField lengthTF = new JTextField();
	    midP.add(nameTF);
	    midP.add(originTF);
	    midP.add(lengthTF);
	    
	    JPanel plusP = new JPanel();
	    JButton plusB = new JButton("+");
	    plusP.add(plusB, BorderLayout.CENTER);
	    plusB.addActionListener(new ActionListener() { 
	    	public void actionPerformed(ActionEvent e)
	        {
	    		
	        }
	    });
	    
	    JPanel saveP = new JPanel();
	    JButton saveB = new JButton("Save");
	    saveP.add(saveB, BorderLayout.CENTER);	
	    saveB.addActionListener(new ActionListener() { 
	    	public void actionPerformed(ActionEvent e)
	        {
	    		Info info = new Info();
	    		info.setName(titleTF.getText());
	    		
	    		try {
	    			FillAndSaveFile(info);
	    		} 
	    		catch (Exception ex) {
	    			ex.printStackTrace();
	    		}
	        }
	    	
			public void FillAndSaveFile(Info info) {
				List<String> result = new ArrayList<String>();
				result.add("/* " + info.getName() + "*/");
				SaveFile(result);
	    	}
			
			public void SaveFile(List<String> result) {
				FileDialog fd = new FileDialog(new JFrame(), "Save file", FileDialog.SAVE);
				fd.setDirectory("C:\\");
				fd.setVisible(true);
				List<String> lines = result;
				Path file = Paths.get(fd.getDirectory(), fd.getFile());
				try {
					Files.write(file, lines, Charset.forName("Unicode"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    });
	    JPanel mainP = new JPanel(new GridLayout(4, 0));
	    mainP.add(upperP);
	    midP.setSize(60, 20);
	    mainP.add(midP);
	    mainP.add(plusP);
	    mainP.add(saveP);

	    
	    JFrame frame = new JFrame("Sample");

	    frame.add( mainP , BorderLayout.CENTER);
	    frame.setLocationRelativeTo(null);
	    frame.setSize(300, 250);
	    frame.setResizable(false);
	    frame.setJMenuBar(menuBar);
	    frame.setVisible(true);
	}
}
