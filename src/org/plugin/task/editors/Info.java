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
	
    private String title;
    public String getTitle() { return this.title; }
    public void setTitle(String title) 
    { 
    	this.title = title;
    }
    
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
    
    private String errorText;
    public String getErrorText() {return this.errorText; }
    
	public Info() {
		
	}
	
	public ArrayList<String> InfoToList()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add("/* " + this.getTitle() + " */");
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
		if (title == "") 
		{
			this.errorText = "Please, fill the title field";
			return false;
		}
		for(Entry<String, String[]> memoryElement: this.Memory.entrySet())
		{
			if ((memoryElement.getKey()=="")||(memoryElement.getValue()[0]=="")||(memoryElement.getValue()[1]==""))
			{
				this.errorText = "Please, fill all of the memory fields";
				return false;
			}
			try
			{
				Integer.parseInt(memoryElement.getValue()[0], 16);
			}
			catch(NumberFormatException nfe)
			{
				this.errorText = "All adresses must be hexadecimal (error in memory adresses)";
				return false;
			}
		}
		if ((aliases[0]=="")||(aliases[1]=="")||(aliases[2]=="")||(aliases[3]==""))
		{
				this.errorText = "Please, fill all of the alias fields";
				return false;
		}
		if ((this.stackTop=="")||(this.endHeap==""))
		{
			this.errorText = "Please, fill all of the precharacters fields";
			return false;
		}
		else 
		{
			try
			{
				Integer.parseInt(this.stackTop, 16);
				Integer.parseInt(this.endHeap, 16);
			}
			catch(NumberFormatException nfe)
			{
				this.errorText = "All adresses must be hexadecimal (error in precharacters adresses)";
				return false;
			}
		}
		return true;
	}
	
	public void SaveFile() 
	{
		FileDialog fileDialog = new FileDialog(new JFrame(), "Save file", FileDialog.SAVE);
		// TODO File filter (save *.x files)
		// TODO FileDialog crash!
		try 
		{
			fileDialog.setDirectory("C:\\");
			fileDialog.setVisible(true);
			Path file = Paths.get(fileDialog.getDirectory(), fileDialog.getFile());
			Files.write(file, this.getFileText(), Charset.forName("Unicode"));
		} 
		catch (IOException ex) 
		{
				this.errorText = "File was not saved: " + ex.getMessage();
				fileDialog.setVisible(false);
		}
		fileDialog.dispose();
	}
}


