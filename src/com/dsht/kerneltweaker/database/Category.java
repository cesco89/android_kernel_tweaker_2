package com.dsht.kerneltweaker.database;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="Categories")
public class Category extends Model { 
    
    @Column(name = "mId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public int mId;
    
    @Column(name = "Name", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;

    public List<Item> getItems() {
        return getMany(Item.class, "Category");
    }

    public Category() {
        super();
    }
    
    public Category(int id,String name) {
        super();
        this.mId = id;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Category) {
            Category temp = (Category) o;
            if(temp.name == this.name) {
                return true;
            }
            return false;
        }
        return false;
    }

}
