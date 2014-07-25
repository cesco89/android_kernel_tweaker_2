package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.UiHelpers;

import android.content.Context;
import android.util.AttributeSet;

public class ListPreference extends GenericPreference{

    
    private String[] entries, values;

    public ListPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference);
    }
    
    public ListPreference(Context context, String key) {
        super(context);
        setLayoutResource(R.layout.preference);
        init(context, key);
    }

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference);
    }

    public ListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.preference);
    }

    
    
    @Override
    public void onClick() {
        getDialog().show();
    }
    
    
    
    public void setEntries(String[] entries) {
        this.entries = entries;
    }
    
    public void setValues(String[] values) {
        this.values = values;
    }

}
