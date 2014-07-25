package com.dsht.kerneltweaker.utils;

public class UvItem {

    private String mName;
    private String mValue;
    
    public UvItem() { 
        
    }
    
    public UvItem(String name, String value) {
        this.mName = name;
        this.mValue = value;
    }
    
    public String getName() {
        return this.mName;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public void setName(String name) {
        this.mName = name;
    }
    
    public void setValue(String value) {
        this.mValue = value;
    }

}
