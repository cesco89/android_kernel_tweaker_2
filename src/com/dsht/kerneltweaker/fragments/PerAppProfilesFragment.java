package com.dsht.kerneltweaker.fragments;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.ProfilePreferenceActivity;
import com.dsht.kerneltweaker.adapters.ProfilesBaseAdapter;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kerneltweaker.utils.UiHelpers;

public class PerAppProfilesFragment extends Fragment {
    
    private UiHelpers mUiHelpers;
    private ListView mList;
    private ProfilesBaseAdapter mAdapter;
    private List<Profile> mProfiles;
    private DatabaseHelpers mDb;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mUiHelpers = new UiHelpers(getActivity());
        mDb = new DatabaseHelpers();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.per_app_list_profiles, container, false);
        mList = (ListView) v.findViewById(R.id.list);
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mProfiles = mDb.getAllProfiles();
        mAdapter = new ProfilesBaseAdapter(getActivity(), mProfiles);
        mList.setAdapter(mAdapter);
        
    }

    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { 
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_new, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
        case R.id.action_new:
            mUiHelpers.startActivity(getActivity(), ProfilePreferenceActivity.class);
            break;
        }      
        return super.onOptionsItemSelected(item);
    }
}
