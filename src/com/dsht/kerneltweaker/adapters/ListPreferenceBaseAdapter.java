package com.dsht.kerneltweaker.adapters;

import com.dsht.kerneltweaker.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListPreferenceBaseAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mEntries;
    private String previousValue;

    public ListPreferenceBaseAdapter(Context c, String[] entries, String prevValue) {
        this.mContext = c;
        this.mEntries = entries;
        this.previousValue = prevValue;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mEntries.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mEntries[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public void setValue(String value) {
        this.previousValue = value;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.list_preference_dialog_row, parent, false);
        TextView text = (TextView) v.findViewById(R.id.textView);

        Typeface tf = Typeface.create("sans-serif-thin", Typeface.NORMAL);
        Typeface tfBold = Typeface.create("sans-serif-bold", Typeface.NORMAL);
        if(mEntries[position].equals(previousValue)) {
            text.setTypeface(tfBold);
        }else {
            text.setTypeface(tf);
        }
        text.setText(mEntries[position]);

        return v;
    }

}
