package com.dsht.kerneltweaker.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name="User_items")
public class UserItem extends Model {
    
    @Column(name = "Name",index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;
    
    @Column(name = "Summary", index = true)
    public String summary;
    
    @Column(name = "Ui_type", index = true)
    public int ui_type;
    
    @Column(name="File_path", index = true)
    public String filePath;
    
    @Column(name="Entries_path", index = true)
    public String entriesPath;
    
    @Column(name="value_on", index = true)
    public String valueOn;
    
    @Column(name="value_off", index = true)
    public String valueOff;
    
    @Column(name="separator", index = true)
    public String separator;
    
    public UserItem() {
        super();
    }
    
    public UserItem(String name, String summ, int type, String fPath, String ePath, String valOn, String valOff, String sep) {
        this.name = name;
        this.summary = summ;
        this.ui_type = type;
        this.filePath = fPath;
        this.entriesPath = ePath;
        this.valueOn = valOn;
        this.valueOff = valOff;
        this.separator = sep;
    }
    
    
    public boolean summaryEquals(Object o) {
        if(o instanceof UserItem) {
            UserItem temp = (UserItem)o;
            if(this.summary == temp.summary) {
                return true;
            }
            return false;
        }
        return false;
    }
    
}
