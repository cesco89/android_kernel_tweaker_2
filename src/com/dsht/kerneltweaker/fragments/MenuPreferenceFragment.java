package com.dsht.kerneltweaker.fragments;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.MainActivity;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.GreenPreferenceCategory;
import com.dsht.kerneltweaker.widgets.MenuPreference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuPreferenceFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private GreenPreferenceCategory mStatsCategory;
    private GreenPreferenceCategory mDeviceCategory;
    private GreenPreferenceCategory mCustomCategory;
    
    private MenuPreference mCpuStats;
    private MenuPreference mCpu;
    private MenuPreference mGpu;
    private MenuPreference mUv;
    private MenuPreference mKernel;
    private UiHelpers mUiHelpers;
    private Config mConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.menu_headers);
        mUiHelpers = new UiHelpers(getActivity());
        mConfig = Config.getInstance();
        
        mStatsCategory = (GreenPreferenceCategory) findPreference(Config.KEY_HEADER_STATS);
        mDeviceCategory = (GreenPreferenceCategory) findPreference(Config.KEY_HEADER_DEVICE);
        mCustomCategory = (GreenPreferenceCategory) findPreference(Config.KEY_HEADER_CUSTOM);
        
        mCpuStats = (MenuPreference) findPreference(Config.KEY_STATS_CPU);
        mCpu = (MenuPreference) findPreference(Config.KEY_CPU);
        mGpu = (MenuPreference) findPreference(Config.KEY_GPU);
        mUv = (MenuPreference) findPreference(Config.KEY_UV);
        mKernel = (MenuPreference) findPreference(Config.KEY_KERNEL);

        mCpuStats.setOnPreferenceClickListener(this);
        mCpu.setOnPreferenceClickListener(this);
        mGpu.setOnPreferenceClickListener(this);
        mUv.setOnPreferenceClickListener(this);
        mKernel.setOnPreferenceClickListener(this);
        
        if(mConfig.getUvPath() == null) {
            mDeviceCategory.removePreference(mUv);
        }

        setRetainInstance(true);   
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.menu_list, container,false);
        return v;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        // TODO Auto-generated method stub
        if(UVPreferenceFragment.getLayoutListener() != null) {
            UVPreferenceFragment.removeListener();
        }
        Fragment f = null;
        if(pref == mCpuStats) {
            f = new CpuStatsFragment();
        }
        if(pref == mCpu) {
            f = new CpuPreferenceFragment();
        }
        if(pref == mGpu) {
            f = new GpuPreferenceFragment();
        }
        if(pref == mUv) {
            f = new UVPreferenceFragment();
        }
        if(pref == mKernel) {
            f = new KernelPreferenceFragment();
        }
        if(f != null) {
            mUiHelpers.loadFragment(f);
        }
        return false;
    }


}
