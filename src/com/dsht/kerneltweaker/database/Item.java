package com.dsht.kerneltweaker.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Boot_Items")
public class Item extends Model {
    
    @Column(name = "Name",index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;
    
    @Column(name = "FilePath", index = true)
    public String filePath;
    
    @Column(name = "Value", index = true)
    public String value;

    public Item() {
        super();
    }
    
    public Item(String name, String filePath, String value) {
        super();
        this.name = name;
        this.filePath = filePath;
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Item) {
            Item temp = (Item) o;
            if(temp.name == this.name && temp.filePath == this.filePath) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    

}
