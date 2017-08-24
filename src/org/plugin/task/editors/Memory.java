package org.plugin.task.editors;

public class Memory {

	private String name;
    public String getName() { return this.name; }
    public void setName(String name) 
    { 
    	this.name = name;
    }
    
    private Long decOrigin;
    public Long getDecOrigin() { return this.decOrigin; }
    public void setDecOrigin(Long origin) 
    { 
    	this.decOrigin = origin;
    }

	private String hexOrigin;
    public String getHexOrigin() { return this.hexOrigin; }
    public void setHexOrihin(String origin) 
    { 
    	this.hexOrigin = origin;
    }  

    private Long length;
    public Long getLength() { return this.length; }
    public void setLength(Long length) 
    { 
    	this.length = length;
    }

	private Character unit;
    public Character getUnit() { return this.unit; }
    public void setUnit(Character unit) 
    { 
    	this.unit = unit;
    }

	private Long decUnit;
    public Long getDecUnit() { return this.decUnit; }
    public void setDecUnit(Long decUnit) 
    { 
    	this.decUnit = decUnit;
    }
}
