package org.plugin.task.editors;

import java.awt.FileDialog;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

public class Info {
	
    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    
    public Map<String, String[]> Memory;
    
    private String[] aliases;
    public String[] getAliases() { return this.aliases; }
    public void setAliases(String[] aliases) { this.aliases = aliases; }
    
    private String endHeap;
    public String getEndHeap() { return this.endHeap; }
    public void setEndHeap(String endHeap) { this.endHeap = endHeap; }
    
    private String stackTop;
    public String getStackTop() { return this.stackTop; }
    public void setStackTop(String stackTop) { this.stackTop = stackTop; }
    
    private ArrayList<String> fileText;
    public ArrayList<String> getFileText() { return this.fileText; }
    public void setFileText(ArrayList<String> fileText) { this.fileText = fileText; }
    
	public Info() {
		
	}
	
	public ArrayList<String> InfoToList()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add("/* " + this.getName() + " */");
		result.add("");
		result.add("MEMORY\r\n{");
		for(Entry<String, String[]> memoryElement: this.Memory.entrySet())
		{
			result.add("\t" + memoryElement.getKey() + " : ORIGIN = " + memoryElement.getValue()[0] + ", LENGTH = " + memoryElement.getValue()[1]);
		}
		result.add("}");
		result.add("");
		result.add("REGION_ALIAS(\"startup\", " + this.getAliases()[0] + ")");
		result.add("REGION_ALIAS(\"text\", " + this.getAliases()[1] + ")");
		result.add("REGION_ALIAS(\"data\", " + this.getAliases()[2] + ")");
		result.add("REGION_ALIAS(\"sdata\", " + this.getAliases()[3] + ")");
		result.add("");
		result.add("PROVIDE (__stack_top = (" + this.getStackTop() + " & -4) );");
		result.add("PROVIDE (__end_heap = (" + this.getStackTop() + ") );");
		return result;
	}
	
	public boolean CheckData()
	{
		//TODO
		return true;
	}
	
	public void SaveFile() 
	{
		FileDialog fileDialog = new FileDialog(new JFrame(), "Save file", FileDialog.SAVE);
		fileDialog.setDirectory("C:\\");
		fileDialog.setVisible(true);
		// TODO File filter (save *.x files)
		//TODO crashes if we just close FileDialog
		Path file = Paths.get(fileDialog.getDirectory(), fileDialog.getFile());
		try 
		{
			Files.write(file, this.getFileText(), Charset.forName("Unicode"));
		} 
		catch (IOException ex) 
		{
			//TODO exception
		}
	}

}
