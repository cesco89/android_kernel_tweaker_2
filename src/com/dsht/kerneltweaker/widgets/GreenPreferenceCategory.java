package com.dsht.kerneltweaker.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class GreenPreferenceCategory extends PreferenceCategory {
	
	Context mContext;
	
    public GreenPreferenceCategory(Context context) {
        super(context);
        this.mContext = context;
    }

    public GreenPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public GreenPreferenceCategory(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.parseColor("#27af89"));
        titleView.getBackground().setColorFilter(Color.parseColor("#27af89"), Mode.SRC_ATOP);
    }
}
