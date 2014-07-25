package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.R;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;

public class EditPreference extends GenericPreference{
    
    private AlertDialog mDialog;
    
    public EditPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference);
    }
    
    public EditPreference(Context context, String key) {
        super(context);
        this.setKey(key);
    }
    
    public EditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public EditPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public EditPreference(Context context, String key, boolean fromJava) {
        super(context);
        init(context, key, fromJava);
    }
    
    public EditPreference(Context context,String key, String title, String summary) {
        super(context);
        setLayoutResource(R.layout.preference);
        this.setKey(key);
        this.setTitle(title);
        this.setSummary(summary);
    }
    
    @Override
    public void onClick() {
        super.onClick();
        getDialog().show();
    }

}
