package org.plugin.task.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Info {
	
    private String title;
    public String getTitle() { return this.title; }
    public void setTitle(String title) 
    { 
    	this.title = title;
    }
    
    private Map<String, Memory> memories;
    public Map<String, Memory> getMemories()
    {
    	return this.memories;
    }
    public void setMemories(Map<String, Memory> memories)
    {
    	this.memories = memories;
    }
    
    /*
	*Takes name, origin and length as strings
	*/
    public void addMemory(String name, String[] memoryData) throws Exception
    {
    	Memory memory = new Memory();
    	try 
    	{
    		memory.setName(memoryData[0]);
    		memory.setDecOrigin(decFromString(memoryData[1]));
    		memory.setHexOrihin("0x" + fillHex(memory.getDecOrigin()));
    		String canonicLength = getUnit(memoryData[2]);
    		memory.setLength(Long.parseLong(canonicLength.substring(0, canonicLength.length()-1)));
    		memory.setUnit(canonicLength.charAt(canonicLength.length()-1));
			switch (memory.getUnit())
			{
				case 'B': memory.setDecUnit((long)1);
		  				  break;
				case 'K': memory.setDecUnit((long)1024);
						  break;
				case 'M': memory.setDecUnit((long)1024*1024);
				  		  break;
				case 'G': memory.setDecUnit((long)1024*1024*1024);
		  		  		  break;
			}
    		this.memories.put(name, memory);
    	}
    	catch(Exception ex)
    	{
    		throw ex;
    	}
    }
    
    private String startupAlias;
    public String getStartupAlias() { return this.startupAlias; }
    public void setStartupAlias(String startupAlias) 
    { 
    	this.startupAlias = startupAlias;
    }
    
    private String textAlias;
    public String getTextAlias() { return this.textAlias; }
    public void setTextAlias(String textAlias) 
    { 
    	this.textAlias = textAlias;
    }
    
    private String dataAlias;
    public String getDataAlias() { return this.dataAlias; }
    public void setDataAlias(String dataAlias) 
    { 
    	this.dataAlias = dataAlias;
    }
    
    private String sdataAlias;
    public String getSdataAlias() { return this.sdataAlias; }
    public void setSdataAlias(String sdataAlias) 
    { 
    	this.sdataAlias = sdataAlias;
    }
    
    /*
     * Takes String[] { startup, text, data, sdata }
     */
    public void setAliases(String startup, String text, String data, String sdata) 
    { 
    	this.startupAlias = startup; 
    	this.textAlias = text; 
    	this.dataAlias = data; 
    	this.sdataAlias = sdata; 
    }
    
    private Long stackTop;
    public String getStackTop() 
    { 
    	return "0x" + fillHex(this.stackTop); 
    }
    public void setStackTop(String stackTop) 
    {
    	if (stackTop!="")
    	{
    		this.stackTop = decFromString(stackTop);
    	}
    }
    
    private Long endHeap;
    public String getEndHeap() 
    { 
    	return "0x" + fillHex(this.endHeap); 
    }
    public void setEndHeap(String endHeap) 
    { 
    	if (endHeap!="")
    	{
    		this.endHeap = decFromString(endHeap); 
    	}
    }
    
    private ArrayList<String> fileText;
    public ArrayList<String> getFileText() { return this.fileText; }
    public void setFileText(ArrayList<String> list) { this.fileText = list; }
    
    private String errorText;
    public String getErrorText() {return this.errorText; }
    public void clearErrorText() { this.errorText = ""; };
    
	public Info() {	}

	public void CreateFromFile() throws Exception
	{
		parseFile();
	}
	
	public boolean CheckData()
	{
		Integer memoryDivider = 4*1024; //divider for memory origin and length
		//Title check
		if (title == "") 
		{
			this.errorText = "Please, fill the title field";
			return false;
		}
		
		//Memory check
		for(Entry<String, Memory> memoryElement: this.memories.entrySet())
		{
			Memory memory = memoryElement.getValue();
			//is it filled?
			if ((memoryElement.getKey()=="")||(memory.getHexOrigin()=="")
					||(memory.getLength()==null))
			{
				this.errorText = "Please, fill all of the memory fields";
				return false;
			} 
			else 
			{
				//is name correct?
				if (!isLatinLetter(memoryElement.getKey().charAt(0)))
				{
					this.errorText = "First letter of memory name shall be a latin letter";
					return false;
				}
				if (!memoryElement.getKey().matches("[a-zA-Z0-9]*"))
				{
					this.errorText = "Memory name shall contain only latin letters and numbers";
					return false;
				}
				//is it normal size?
				if (memory.getHexOrigin().length()>10)
				{
					this.errorText = "Too big memory origin value (up to 8 order in hex is permited)";
					return false;
				}
				//is it multiple of 4K?
				if (!isMultiple(memory.getDecOrigin(), memoryDivider))
				{
					this.errorText = "Memory origin shall be a multiple of 4K";
					return false;
				}
				//Fits into 32-bit space address?
				try 
				{
					Long value = memory.getDecOrigin() + memory.getDecUnit()*memory.getLength();
					if (value>Math.pow(2, 32))
					{
						this.errorText = "The section must fit into the 32-bit address space";
						return false;
					}
				}
				catch(Exception ex)
				{
					this.errorText = "The section must fit into the 32-bit address space";
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
			if (!(this.memories.containsKey(this.startupAlias)&&this.memories.containsKey(this.textAlias)
					&&this.memories.containsKey(this.dataAlias)&&this.memories.containsKey(this.sdataAlias)))
			{
				this.errorText = "Alias fields must be filled with real memory data";
				return false;
			}
		}
		
		//Predefined characters check
		//is it filled?
		if ((this.stackTop==null)||(this.endHeap==null))
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
					long origin = this.memories.get(this.dataAlias).getDecOrigin();
					long memoryLength = this.memories.get(this.dataAlias).getLength() *
							this.memories.get(this.dataAlias).getDecUnit();
					if ((this.stackTop<origin)||(this.stackTop>origin + memoryLength)
							||(this.endHeap<origin)||(this.endHeap>origin + memoryLength))
					{
						this.errorText = 
								"Predefined characters values shall be located in \r\n"
								+ "section of memory data alias is attached to";
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

	public ArrayList<String> InfoToList()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add("/* " + this.getTitle() + " */");
		result.add("");
		result.add("MEMORY\r\n{");
		for(Entry<String, Memory> memoryElement: this.memories.entrySet())
		{
			result.add("\t" + memoryElement.getKey() + " : ORIGIN = " 
					+ memoryElement.getValue().getHexOrigin() + ", LENGTH = " + 
					memoryElement.getValue().getLength() + memoryElement.getValue().getUnit());
		}
		result.add("}");
		result.add("");
		result.add("REGION_ALIAS(\"startup\", " + this.startupAlias + ")");
		result.add("REGION_ALIAS(\"text\", " + this.textAlias + ")");
		result.add("REGION_ALIAS(\"data\", " + this.dataAlias + ")");
		result.add("REGION_ALIAS(\"sdata\", " + this.sdataAlias + ")");
		result.add("");
		result.add("PROVIDE (__stack_top = (0x" + fillHex(this.stackTop) + " & -4) );");
		result.add("PROVIDE (__end_heap = (0x" + fillHex(this.endHeap) + ") );");
		return result;
	}

	/*
	 * Parses this.fileText
	 */
	private void parseFile() throws Exception
	{
		this.memories = new HashMap<String, Memory>();
		Pattern pattern;
		Matcher matcher;
		this.title = "";
		this.stackTop = (long) 0;
		this.endHeap = (long) 0;
		this.startupAlias = "";
		this.textAlias = "";
		this.dataAlias = "";
		this.sdataAlias = "";
		
		for(String line: this.fileText)
		{
			//Title?
			if (line.matches("/\\*.*\\*/"))
			{
				pattern = Pattern.compile("/\\*(.*)\\*/");
				matcher = pattern.matcher(line);
				if (matcher.find())
				{
					   this.title = matcher.group(1).trim();
				}
				continue;
			}
			
			//Memory?
			if (line.matches(".*:.*ORIGIN.*=.*,.*LENGTH.*=.*"))
			{
				String name = "";
				String origin = "";
				String length = "";
					
				pattern = Pattern.compile("(.*):");
				matcher = pattern.matcher(line);
				if (matcher.find())
				{
					name = matcher.group(1).trim();
				}
	
				pattern = Pattern.compile(".*ORIGIN.*=(.*),.*");
				matcher = pattern.matcher(line);
				if (matcher.find())
				{
					origin = matcher.group(1).trim();
				}
					
				pattern = Pattern.compile(".*LENGTH.*=(.*)$");
				matcher = pattern.matcher(line);
				if (matcher.find())
				{
					length = matcher.group(1).trim();
				}
				this.addMemory(name, new String[] { name, origin, length });
				continue;
			}
			
			//Alias?
			if (line.matches(".*REGION_ALIAS.*\\(.*\".*\".*,.*\\)"))
			{
				String value = "";
				pattern = Pattern.compile(".*REGION_ALIAS.*\\(.*,(.*)\\)$");
				matcher = pattern.matcher(line);
				if (matcher.find())
				{
					value = matcher.group(1).trim();
					if (line.matches(".*REGION_ALIAS.*\\(.*\".*startup.*\".*,.*\\)"))
					{
						this.startupAlias = value;
					}
					else if (line.matches(".*REGION_ALIAS.*\\(.*\".*text.*\".*,.*\\)"))
					{
						this.textAlias = value;
					}
					else if (line.matches(".*REGION_ALIAS.*\\(.*\"data.*\".*,.*\\)"))
					{
						this.dataAlias = value;
					}
					else if (line.matches(".*REGION_ALIAS.*\\(.*\".*sdata.*\".*,.*\\)"))
					{
						this.sdataAlias = value;
					}
				}
				continue;
			}
			
			//Predefined characters?
			if (line.matches(".*PROVIDE.*(.*=.*(.*).*).*"))
			{
				String value = "";
				if (line.matches(".*PROVIDE.*\\(.*__stack_top.*=.*\\(.*&.*\\).*\\).*"))
				{
					try 
					{
						pattern = Pattern.compile(".*PROVIDE.*\\(.*=.*\\((.*)&.*\\).*\\).*");
						matcher = pattern.matcher(line);
						if (matcher.find()) 
						{
							value = matcher.group(1).trim();
							this.stackTop = decFromString(value);
						}
					}
					catch(Exception ex)
					{
						this.errorText = ex.getMessage();
					}
				}
				else if (line.matches(".*PROVIDE.*(.*__end_heap.*=.*(.*).*).*"))
					{
						try 
						{
							pattern = Pattern.compile(".*PROVIDE.*\\(.*=.*\\((.*)\\).*\\).*");
							matcher = pattern.matcher(line);
							if (matcher.find()) 
							{
								value = matcher.group(1).trim();
								this.stackTop = decFromString(value);
							}
						}
						catch(Exception ex)
						{
							this.errorText = ex.getMessage();
						}
						
					}
				continue;
			}
		}
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
					return Long.parseLong(string.substring(2), 16);
			}
			else
			{
				return Long.parseLong(string);
			}
		}
		catch(Exception ex)
		{
			this.errorText = "All adresses must be decimal or hexadecimal\r\n"
					+ "and be up to 9223372036854775807 in dec";
			return null;
		}
	}
	
	/*
	 * Checks if char is a latin letter
	 */
	private boolean isLatinLetter(char c) {
	    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	
	private String getUnit(String length) throws Exception
	{
		String currentLength = length;
		Character memoryUnit = currentLength.charAt(currentLength.length()-1);
		List<Character> memoryUnits = Arrays.asList('g', 'G', 'm', 'M', 'k', 'K', '�', '�', '�', 
										'0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
		if (!memoryUnits.contains(memoryUnit)) 
				
		{
			throw new Exception("Wrong memory length unit. Length must be not specified, or \r\n"
					+ "specified as K (kilobites), M (megabyte) or G (gigabyte)");
		}
		else 
		{
			if ((Arrays.asList( 'g', 'G' )).contains(memoryUnit))
			{
				currentLength =  currentLength.substring(0, currentLength.length()-1) + "G";
			}
			else if ((Arrays.asList( 'm', 'M', '�' )).contains(memoryUnit))
			{
				currentLength =  currentLength.substring(0, currentLength.length()-1) + "M";
			}
			else if ((Arrays.asList('k', 'K', '�', '�' )).contains(memoryUnit))
			{
				currentLength =  currentLength.substring(0, currentLength.length()-1) + "K";
			}
			else
			{
				currentLength = decFromString(currentLength) + "B";
			}
			try 
			{
				Long value = Long.parseLong(currentLength.substring(0, currentLength.length()-1));
				if (value<=0)
				{
					throw new Exception("Length value shall be positive :)");
				}
			}
			catch(Exception ex)
			{
				throw new Exception("Length value shall be numerical (in hex or dec)");
			}
			return currentLength;
		}
	}
	
	/*
	 * Checks if value is multiple of 4K
	 */
	private boolean isMultiple(Long number, Integer divider)
	{
		long result = number & divider;
		return (result==0);
	}
	/*
	 * fillHex method adds zeros to the beginning of input address
	 */
	private String fillHex(Long decValue)
	{
		String hexValue = Long.toHexString(decValue);
		if (hexValue.length()<=8)
		{
			return ("00000000" + hexValue).substring(hexValue.length());
		}
		else 
		{
			return hexValue;
		}
	}
}


