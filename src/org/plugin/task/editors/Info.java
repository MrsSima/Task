package org.plugin.task.editors;

import java.util.Map;

public class Info {
	
    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    
    public Map<String, String[]> Memory;
    
    public String[] Aliases;
    
    private String endHeap;
    public String getEndHeap() { return this.endHeap; }
    public void setEndHeap(String endHeap) { this.endHeap = endHeap; }
    
    private String stackTop;
    public String getStackTop() { return this.stackTop; }
    public void setStackTop(String stackTop) { this.stackTop = stackTop; }
    
	public Info() {
		
	}

}
