package org.plugin.task.editors;

import java.util.Map;

public class Info {
	
    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    
    public Map<String, String[]> Memory;
    
    public Map<String, String> Aliases;
    
	public Info() {
		
	}

}
