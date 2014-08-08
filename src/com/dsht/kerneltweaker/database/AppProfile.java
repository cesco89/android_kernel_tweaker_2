package com.dsht.kerneltweaker.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;

public class AppProfile extends Model {

    public AppProfile() {
        super();
    }
    
    public AppProfile(String packName, Profile profile) {
        this.packageName = packName;
        this.profile = profile;
    }
    
    @Column(name = "PackageName",index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String packageName;
    
    @Column(name = "Profile", index = true, onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public Profile profile;

}
