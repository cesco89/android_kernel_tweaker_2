package com.dsht.kerneltweaker.adapters;

import java.util.List;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.ProfilePreferenceActivity;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kerneltweaker.utils.UiHelpers;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProfilesBaseAdapter extends BaseAdapter {

    private List<Profile> mProfiles;
    private Context mContext;
    private UiHelpers mUiHelpers;

    public ProfilesBaseAdapter(Context con, List<Profile> profiles) {
        // TODO Auto-generated constructor stub
        this.mContext = con;
        this.mProfiles = profiles;
        this.mUiHelpers = new UiHelpers(con);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mProfiles.size();
    }

    @Override
    public Profile getItem(int arg0) {
        // TODO Auto-generated method stub
        return mProfiles.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return mProfiles.get(arg0).hashCode();
    }
    
    public void remove(int position) {
        mProfiles.remove(position);
        this.notifyDataSetChanged();
    }
    

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.profiles_list_row, parent, false);
        }

        TextView mTitle = (TextView) convertView.findViewById(R.id.title);
        ImageButton mEdit = (ImageButton) convertView.findViewById(R.id.edit);
        ImageButton mDelete = (ImageButton) convertView.findViewById(R.id.delete);
        
        final Profile mProfile = getItem(position);
        mTitle.setText(mProfile.name);
        
        mEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               Bundle b = new Bundle();
               b.putString(Config.BUNDLE_PROFILE_NAME, mProfile.name);
               b.putString(Config.BUNDLE_PROFILE_MAX, mProfile.maxFreq);
               b.putString(Config.BUNDLE_PROFILE_MIN, mProfile.minFreq);
               b.putString(Config.BUNDLE_PROFILE_GOVERNOR, mProfile.governor);
               mUiHelpers.startActivity((Activity) mContext, ProfilePreferenceActivity.class, b);
               b = null;
            }
            
        });
        
        mDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mUiHelpers.buildDeleteProfileDialog(mProfile, ProfilesBaseAdapter.this, position).show();
            }
            
        });

        return convertView;
    }

}
