package com.dsht.kerneltweaker.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

public class Profile extends Model {

    @Column(name = "Name",index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;
    
    @Column(name = "MaxFreq", index = true)
    public String maxFreq;
    
    @Column(name = "MinFreq", index = true)
    public String minFreq;
    
    @Column(name = "Governor", index = true)
    public String governor;
    
    public Profile() {
        super();
    }
    
    public Profile(String name, String max, String min, String gov) {
        super();
        this.name = name;
        this.maxFreq = max;
        this.minFreq = min;
        this.governor = gov;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Profile) {
            Profile temp = (Profile) o;
            if(temp.name == this.name) {
                return true;
            }
            return false;
        }
        return false;
    }

}
