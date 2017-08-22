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
    
    public String startupAlias;
    public String textAlias;
    public String dataAlias;
    public String sdataAlias;
    public String[] getAliases() { return new String[] 
    		{this.startupAlias, this.textAlias, this.dataAlias, this.sdataAlias  }; }
  /// <summary>
  /// Takes String[] {startup, text, data, sdata} //TODO
  /// </summary>
    public void setAliases(String[] aliases) 
    { 
    	this.startupAlias = aliases[0]; 
    	this.textAlias = aliases[1]; 
    	this.dataAlias = aliases[2]; 
    	this.sdataAlias = aliases[3]; 
    }
    
    private Long stackTop;
    public Long getStackTop() { return this.stackTop; }
    public void setStackTop(String stackTop) 
    {
    	if (stackTop!="")
    	{
    		this.stackTop = decFromString(stackTop);
    	}
    }
    
    private Long endHeap;
    public Long getEndHeap() { return this.endHeap; }
    public void setEndHeap(String endHeap) 
    { 
    	if (endHeap!="")
    	{
    		this.endHeap = decFromString(endHeap); 
    	}
    }
    
    private ArrayList<String> fileText;
    public ArrayList<String> getFileText() { return this.fileText; }
    public void setFileText(ArrayList<String> fileText) { this.fileText = fileText; }
    
    private String errorText;
    public String getErrorText() {return this.errorText; }
    public void clearErrorText() { this.errorText = ""; };
    
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
			result.add("\t" + memoryElement.getKey() + " : ORIGIN = " 
					+ memoryElement.getValue()[0] + ", LENGTH = " + memoryElement.getValue()[1]);
		}
		result.add("}");
		result.add("");
		result.add("REGION_ALIAS(\"startup\", " + this.getAliases()[0] + ")");
		result.add("REGION_ALIAS(\"text\", " + this.getAliases()[1] + ")");
		result.add("REGION_ALIAS(\"data\", " + this.getAliases()[2] + ")");
		result.add("REGION_ALIAS(\"sdata\", " + this.getAliases()[3] + ")");
		result.add("");
		result.add("PROVIDE (__stack_top = (0x" + fillHex(this.stackTop) + " & -4) );");
		result.add("PROVIDE (__end_heap = (0x" + fillHex(this.endHeap) + ") );");
		return result;
	}
	
	public boolean CheckData()
	{
		//Title check
		if (title == "") 
		{
			this.errorText = "Please, fill the title field";
			return false;
		}
		
		//Memory check
		for(Entry<String, String[]> memoryElement: this.Memory.entrySet())
		{
			//is it filled?
			if ((memoryElement.getKey()=="")||(memoryElement.getValue()[0]=="")
					||(memoryElement.getValue()[1]==""))
			{
				this.errorText = "Please, fill all of the memory fields";
				return false;
			} 
			else 
			{
				//is it normal size?
				if (memoryElement.getValue()[0].length()>10)
				{
					this.errorText = "Too big memory origin value (up to 8 order in hex is permited)";
					return false;
				}
				//is it dec or hexdec? write hex string to memory map
				try
				{
					Long decValue = decFromString(memoryElement.getValue()[0]);
					String newOrigin = "0x" + fillHex(decValue);
					this.Memory.put(memoryElement.getKey(), 
							new String[] { newOrigin, memoryElement.getValue()[1] });
				}
				catch(NumberFormatException nfe)
				{
					this.errorText = "All adresses must be decimal or hexadecimal (error in memory adresses)";
					return false;
				}
				//is length format right?
				String currentLength = memoryElement.getValue()[1];
				if ((Character.toUpperCase(currentLength.charAt(currentLength.length()-1))!='K')
						&&(Character.toUpperCase(currentLength.charAt(currentLength.length()-1))!='M'))
						
				{
					this.errorText = 
							"Wrong memory length unit. Length must be specified as K (kilobites) or M (megabytes)";
					return false;
				}
				try 
				{
					Integer.parseInt(currentLength.substring(0, currentLength.length()-1));
				}
				catch(Exception ex)
				{
					this.errorText = 
							"The value of length shall be numerical with designation of the size at the end (ex. 32K)";
					return false;
				}
			}
		}
		
		//Aliases check
		//is it filled?
		if ((this.startupAlias=="")||(this.textAlias=="")
				||(this.dataAlias=="")||(this.sdataAlias==""))
		{
				this.errorText = "Please, fill all of the alias fields";
				return false;
		} 
		else
		{
			//is data filled with right memory names?
			if (!(this.Memory.containsKey(this.startupAlias)&&this.Memory.containsKey(this.textAlias)
					&&this.Memory.containsKey(this.dataAlias)&&this.Memory.containsKey(this.sdataAlias)))
			{
				this.errorText = "Alias fields must be filled with real memory data";
				return false;
			}
		}
		
		//Predefined characters check
		//is it filled?
		if ((this.stackTop==0)||(this.endHeap==0))
		{
			this.errorText = "Please, fill all of the precharacters fields";
			return false;
		}
		else 
		{
			//is it normal size?
			if ((Long.toHexString(this.stackTop).length()>10)
					||(Long.toHexString(this.endHeap).length()>10))
			{
				this.errorText = "Too big precharacters values (up to 8 order in hex is permited)";
				return false;
			}
			//are they in right memory section?
			try
			{
					long origin = Long.parseLong(this.Memory.get(this.dataAlias)[0].substring(2), 16);
					long memoryLength = 0;
					int memoryUnits = 0;
					String memoryLengthString = this.Memory.get(this.dataAlias)[1];
					switch (Character.toUpperCase(memoryLengthString.charAt(memoryLengthString.length()-1)))
					{
						case 'K': memoryUnits = 1024;
								  break;
						case 'M': memoryUnits = 1024*1024;
						  		  break;
					}
					memoryLength =  
							Long.parseLong(memoryLengthString.substring(0, memoryLengthString.length()-1)) * memoryUnits;
					if ((this.stackTop<origin)||(this.stackTop>origin + memoryLength)
							||(this.endHeap<origin)||(this.endHeap>origin + memoryLength))
					{
						this.errorText = 
								"Predefined characters values shall be located in section of memory data alias is attached to";
						return false;
					}
					
			}
			catch(Exception ex)
			{
				this.errorText = ex.getMessage();
				return false;
			}
		}
		return true;
	}
	
	/*
	 * decFromString method parses hex or dec string (0x.. it is considered to be hex)
	 */
	private Long decFromString(String string)
	{
		try 
		{
			if (string.toLowerCase().matches("0x[0-9a-f]+"))
			{
				if (string.length()<10) 
				{
					int degree = (int)Math.pow(10, 8-(string.length() - 2));
					return Long.parseLong(string.substring(2), 16) * degree;
				}
				else
				{
					return Long.parseLong(string.substring(2), 16);
				}
			}
			else
			{
				return Long.parseLong(string);
			}
		}
		catch(Exception ex)
		{
			this.errorText = "Non-valid predefined characters addresses. All adresses must be decimal or hexadecimal";
			return null;
		}
	}
	
	/*
	 * fillHex method adds zeros to the beginning of input address
	 */
	private String fillHex(Long decValue)
	{
		String hexValue = Long.toHexString(decValue);
		return ("00000000" + hexValue).substring(hexValue.length());
	}
	
	public void SaveFile() 
	{
		FileDialog fileDialog = new FileDialog(new JFrame(), "Save file", FileDialog.SAVE);
		// TODO FileDialog crash!
		try 
		{
			fileDialog.setDirectory(System.getProperty("user.home"));
			fileDialog.setVisible(true);
			Path file = Paths.get(fileDialog.getDirectory(), fileDialog.getFile());
			Files.write(file, this.getFileText(), Charset.forName("Unicode"));
		} 
		catch (IOException ex) 
		{
				this.errorText = "File was not saved: " + ex.getMessage();
		}
		//frame.setVisible(false);
	}
}


