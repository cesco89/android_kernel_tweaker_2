package com.dsht.kerneltweaker.adapters;

import java.util.List;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.database.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProfileDialogBaseAdapter extends BaseAdapter {

    private List<Profile> mProfiles;
    private Context mContext;
    
    public ProfileDialogBaseAdapter(Context con, List<Profile> profiles) {
        // TODO Auto-generated constructor stub
        this.mContext = con;
        this.mProfiles = profiles;
    }
    
    class ViewHolder {
        TextView mTitle;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mProfiles.size();
    }

    @Override
    public Profile getItem(int position) {
        // TODO Auto-generated method stub
        return mProfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return mProfiles.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dialog_list_item, null, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.mTitle = (TextView) convertView.findViewById(R.id.text);
        }
        
        if(holder.mTitle != null) {
            holder.mTitle.setText(getItem(position).name);
        }
        
        return convertView;
    }

}
