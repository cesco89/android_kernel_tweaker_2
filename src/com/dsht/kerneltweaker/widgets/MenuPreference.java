package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuPreference extends Preference {

    public MenuPreference(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        setLayoutResource(R.layout.preference_header_item);
    }
    
    public MenuPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setLayoutResource(R.layout.preference_header_item);
    }
    
    public MenuPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        setLayoutResource(R.layout.preference_header_item);
    }
    
    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        
        TextView title = (TextView) view.findViewById(android.R.id.title);
        TextView summary = (TextView) view.findViewById(android.R.id.summary);
        ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
        icon.setColorFilter(Color.WHITE);
        
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Condensed.ttf");
        Typeface tf2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Thin.ttf");
        
        title.setTypeface(tf);
        summary.setTypeface(tf2);
        
        
        
    }



}
