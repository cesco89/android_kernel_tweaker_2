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

    public Item getItemByName(String name) {
        return new Select()
        .from(Item.class)
        .where("Name = ?", name)
        .executeSingle();
    }
    
    public List<UserItem> getUserItemsWithHeader(Context c) {
        List<UserItem> list = new ArrayList<UserItem>();
        UserItem header0 = new UserItem();
        header0.name = c.getResources().getString(R.string.radio_list);
        header0.summary = Config.HEADER;
        UserItem header1 = new UserItem();
        header1.name = c.getResources().getString(R.string.radio_edit);
        header1.summary = Config.HEADER;
        UserItem header2 = new UserItem();
        header2.name = c.getResources().getString(R.string.radio_switch);
        header2.summary = Config.HEADER;
        UserItem header3 = new UserItem();
        header3.name = c.getResources().getString(R.string.radio_checkbox);
        header3.summary = Config.HEADER;
        List<UserItem> items0 = getAllUserItems(0);
        List<UserItem> items1 = getAllUserItems(1);
        List<UserItem> items2 = getAllUserItems(2);
        List<UserItem> items3 = getAllUserItems(3);
        if(items0.size() != 0) {
            list.add(header0);
            list.addAll(items0);
        }
        if(items1.size() != 0) {
            list.add(header1);
            list.addAll(items1);
        }
        if(items2.size() != 0) {
            list.add(header2);
            list.addAll(items2);
        }
        if(items3.size() != 0) {
            list.add(header3);
            list.addAll(items3);
        }
        
        return list;
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

}
