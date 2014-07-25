package com.dsht.kerneltweaker.fragments;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.MainActivity;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.EditPreference;
import com.dsht.kerneltweaker.widgets.GenericPreference;
import com.dsht.kerneltweaker.widgets.ListPreference;
import com.dsht.kerneltweaker.widgets.ObservablePreferenceFragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class GpuPreferenceFragment extends ObservablePreferenceFragment {

    private ListPreference mGpuMax;
    private EditPreference mUpThreshold;
    private EditPreference mDownThreshold;
    private Config mConfig;
    private UiHelpers mUiHelpers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_gpu);
        mConfig = Config.getInstance();
        mUiHelpers = new UiHelpers(getActivity());

        mGpuMax = (ListPreference) findPreference(Config.KEY_GPU_MAX_FREQ);
        mUpThreshold = (EditPreference) findPreference(Config.KEY_GPU_UP_THRESHOLD);
        mDownThreshold = (EditPreference) findPreference(Config.KEY_GPU_DOWN_THRESHOLD);
        if(mConfig.getCurrentGpuMaxFreq() != null) {
            mGpuMax.setSummary((Integer.parseInt(mConfig.getCurrentGpuMaxFreq())/1000000)+" Mhz");
            mGpuMax.setDialog(mUiHelpers.buildListPreferenceDialog(mGpuMax, mConfig.getGpuEntries(), mConfig.getGpuValues()));
            mGpuMax.setFilePath(Config.GPU_MAX_FREQ_FILE);
            mGpuMax.setValue(mConfig.getCurrentGpuMaxFreq());
            mGpuMax.setEntries(mConfig.getGpuEntries());
            mGpuMax.setValues(mConfig.getGpuValues());
        }else {
            this.getPreferenceScreen().removePreference(mGpuMax);
        }

        if(mConfig.getCurrentUpThreshold() != null) {
            mUpThreshold.setSummary(mConfig.getCurrentUpThreshold());
            mUpThreshold.setFilePath(Config.GPU_UP_THRESHOLD);
            mUpThreshold.setDialog(mUiHelpers.buildEditTextDialog(mUpThreshold, mConfig.getCurrentUpThreshold()));
            mUpThreshold.setValue(mConfig.getCurrentUpThreshold());
        }else {
            this.getPreferenceScreen().removePreference(mUpThreshold);
        }

        if(mConfig.getCurrentDownThreshold() != null) {
            mDownThreshold.setSummary(mConfig.getCurrentDownThreshold());
            mDownThreshold.setFilePath(Config.GPU_DOWN_THRESHOLD);
            mDownThreshold.setDialog(mUiHelpers.buildEditTextDialog(mDownThreshold, mConfig.getCurrentDownThreshold()));
            mDownThreshold.setValue(mConfig.getCurrentDownThreshold());
        }else {
            this.getPreferenceScreen().removePreference(mDownThreshold);
        }
        
        if(this.getPreferenceScreen().getPreferenceCount() == 0) {
            
        }


        this.getListener().onComplete();
    }
    
    public void addEmptyPreference() {
        GenericPreference pref = new GenericPreference(getActivity());
        pref.hideBoot(true);
        pref.setTitle(R.string.no_values);
        this.getPreferenceScreen().addPreference(pref);
        
    }

}
