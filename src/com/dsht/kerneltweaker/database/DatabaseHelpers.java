package com.dsht.kerneltweaker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;

public class DatabaseHelpers {

    public DatabaseHelpers() {
        // TODO Auto-generated constructor stub
    }

    public Category saveCategory(int id,String name) {
        Category category = new Category(id,name);
        category.save();
        return category;
    }

    public Item saveItem(String name, String filePath, String value) {
        Item item = new Item(name,filePath, value);
        item.save();
        return item;
    }
    
    public UserItem saveUserItem(String name, String summ, int type, String fPath, String ePath, String valOn, String valOff, String sep) {
        UserItem uItem = new UserItem(name,summ, type, fPath, ePath, valOn, valOff, sep);
        uItem.save();
        return uItem;
    }
    
    public Profile saveProfile(String name, String max, String min, String gov) {
        Profile mProfile = new Profile(name, max, min, gov);
        mProfile.save();
        return mProfile;
    }
    
    public AppProfile saveAppProfile(String packageName, Profile profile) {
        AppProfile mApp = new AppProfile(packageName, profile);
        mApp.save();
        return mApp;
    }
    

    public List<Item> getAllItems(Category category) {
        return new Select()
        .from(Item.class)
        .orderBy("Name ASC")
        .execute();
    }

    public List<Category> getAllCategories(String name) {
        return new Select()
        .from(Category.class)
        .where("Name = ?", name )
        .orderBy("Name ASC")
        .execute();
    }
    
    public List<UserItem> getAllUserItems(int type) {
        return new Select()
        .from(UserItem.class)
        .where("Ui_type = ?", type )
        .orderBy("Name ASC")
        .execute();
    }
    
    public List<UserItem> getAllUserItems() {
        return new Select()
        .from(UserItem.class)
        .orderBy("Name ASC")
        .execute();
    }
    
    public List<Profile> getAllProfiles() {
        return new Select()
        .from(Profile.class)
        .orderBy("Name ASC")
        .execute();
    }
    
    public List<AppProfile> getAllAppProfiles() {
        return new Select()
        .from(AppProfile.class)
        .orderBy("PackageName ASC")
        .execute();
    }

    public Item getItemByName(String name) {
        return new Select()
        .from(Item.class)
        .where("Name = ?", name)
        .executeSingle();
    }
    
    public Profile getProfileByName(String name) {
        return new Select()
        .from(Profile.class)
        .where("Name = ?", name)
        .executeSingle();
    }
    
    public AppProfile getAppProfileByName(String packageName) {
        return new Select()
        .from(AppProfile.class)
        .where("PackageName = ?", packageName)
        .executeSingle();
    }

    public void deleteCategory(String categoryName) {
        new Delete().from(Category.class).where("Name = ?", categoryName).execute();
    }

    public void deleteItem(String itemName) {
        new Delete().from(Item.class).where("Name = ?", itemName).execute();
    }
    
    public void deleteUserItem(UserItem item) {
        String name = item.name;
        new Delete().from(UserItem.class).where("Name = ?", name).execute();
    }
    
    public void deleteProfile(Profile mProfile) {
        String name = mProfile.name;
        new Delete().from(Profile.class).where("Name = ?", name).execute();
    }
    
    public void deleteAppProfile(AppProfile mApp){
        String packName = mApp.packageName;
        new Delete().from(AppProfile.class).where("PackageName = ?", packName).execute();
    }
    

}
