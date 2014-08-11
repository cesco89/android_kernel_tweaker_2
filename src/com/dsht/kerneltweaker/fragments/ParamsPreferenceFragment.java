package com.dsht.kerneltweaker.fragments;

import java.io.File;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.ObservablePreferenceFragment;

import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.util.Log;

public class ParamsPreferenceFragment extends ObservablePreferenceFragment {

    private static String dirPath;
    private PreferenceScreen mRoot;
    private static File mDir;
    private UiHelpers mUiHelpers;
    
    public static ParamsPreferenceFragment newInstance(String dPath) {
        ParamsPreferenceFragment fragment = new ParamsPreferenceFragment();
        dirPath = dPath;
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_container);
        mDir = new File(dirPath);
        mUiHelpers = new UiHelpers(getActivity());
        mRoot = (PreferenceScreen) findPreference(Config.KEY_ROOT);
        Log.d("FILES", dirPath);
        
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mUiHelpers.loadPreferences(mRoot, mDir.listFiles());
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getPreferenceScreen().removeAll();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
    

}
