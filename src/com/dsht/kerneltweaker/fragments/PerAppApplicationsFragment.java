package com.dsht.kerneltweaker.fragments;

import java.util.List;

import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.adapters.AppsBaseAdapter;
import com.dsht.kerneltweaker.adapters.AppsBaseAdapter.AppItem;
import com.dsht.kerneltweaker.database.AppProfile;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.database.Profile;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;

import android.app.Fragment;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PerAppApplicationsFragment extends Fragment implements OnItemClickListener {

    private ListView mList;
    private AppsBaseAdapter mAdapter;
    private List<ResolveInfo> mInstalledApps;
    private List<Profile> mProfiles;
    private DatabaseHelpers mDb;
    private UiHelpers mUiHelpers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mDb = new DatabaseHelpers();
        mInstalledApps = Helpers.getInstalleApps(getActivity());
        mProfiles = mDb.getAllProfiles();
        mUiHelpers = new UiHelpers(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.per_app_list_apps, container, false);
        mList = (ListView) v.findViewById(R.id.list);
        mAdapter = new AppsBaseAdapter(getActivity(), mInstalledApps);
        mAdapter.update();
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.checkChanges();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        AppItem mItem = mAdapter.getItem(arg2);
        if(new DatabaseHelpers().getAllProfiles().size() != 0) {
            mUiHelpers.buildProfileDialog(arg2,mItem, mAdapter).show();
        }
    }

}
