package com.dsht.kerneltweaker.fragments;


import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.ParamsActivity;
import com.dsht.kerneltweaker.interfaces.OnLoadingFinishedListener;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.CheckBoxPreference;
import com.dsht.kerneltweaker.widgets.GenericPreference;
import com.dsht.kerneltweaker.widgets.GreenPreferenceCategory;
import com.dsht.kerneltweaker.widgets.ListPreference;
import com.dsht.kerneltweaker.widgets.ObservablePreferenceFragment;
import com.dsht.kerneltweaker.widgets.SwitchPreference;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CpuPreferenceFragment extends ObservablePreferenceFragment implements OnPreferenceClickListener, OnLoadingFinishedListener{

    private ListPreference mCpuMax;
    private ListPreference mCpuMin;
    private ListPreference mCpuGovernor;
    private GenericPreference mAdvancedGovernor;
    private GenericPreference mAdvancedHotplug;
    private SwitchPreference mCore1, mCore2, mCore3;
    private CheckBoxPreference mMpdec;
    private GreenPreferenceCategory mCoreCategory;
    private GreenPreferenceCategory mAdvancedCategory;
    private String govParamsPath;
    private Config mConfig;
    private UiHelpers mUiHelpers;
    private Resources res;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_cpu);
        res = getActivity().getResources();
        mConfig = Config.getInstance();
        mUiHelpers = new UiHelpers(getActivity());
        
        mCpuMax = (ListPreference) findPreference(Config.KEY_CPU_MAX_FREQ);
        mCpuMin = (ListPreference) findPreference(Config.KEY_CPU_MIN_FREQ);
        mCpuGovernor = (ListPreference) findPreference(Config.KEY_CPU_GOVERNOR);
        mAdvancedGovernor = (GenericPreference) findPreference(Config.KEY_ADVANCED_GOVERNOR);
        mAdvancedHotplug = (GenericPreference) findPreference(Config.KEY_ADVANCED_HOTPLUG);
        mCore1 = (SwitchPreference) findPreference(Config.KEY_CPU_CORE1);
        mCore2 = (SwitchPreference) findPreference(Config.KEY_CPU_CORE2);
        mCore3 = (SwitchPreference) findPreference(Config.KEY_CPU_CORE3);
        mMpdec = (CheckBoxPreference) findPreference(Config.KEY_CPU_MPDEC);
        mCoreCategory = (GreenPreferenceCategory) findPreference(Config.KEY_CORES_CATEGORY);
        mAdvancedCategory = (GreenPreferenceCategory) findPreference(Config.KEY_CPU_ADVANCED_CATEGORY);


        mCpuMax.setSummary((Integer.parseInt(mConfig.getCurrentMaxFreq())/1000)+ " Mhz");
        mCpuMin.setSummary((Integer.parseInt(mConfig.getCurrentMinFreq())/1000)+ " Mhz");
        mCpuGovernor.setSummary(mConfig.getCurrentGovernor());

        mCpuMax.setEntries(mConfig.getCpuFreqEntries());
        mCpuMax.setValues(mConfig.getCpuFreqValues());
        mCpuMax.setFilePath(Config.MAX_FREQ_FILE);
        mCpuMax.setValue(mConfig.getCurrentMaxFreq());
        mCpuMax.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuMax, mConfig.getCpuFreqEntries(), mConfig.getCpuFreqValues()));
        mCpuMax.setMulticore(true);
        mCpuMax.setisMaxFreq(true);

        mCpuMin.setEntries(mConfig.getCpuFreqEntries());
        mCpuMin.setValues(mConfig.getCpuFreqValues());
        mCpuMin.setFilePath(Config.MIN_FREQ_FILE);
        mCpuMin.setValue(mConfig.getCurrentMinFreq());
        mCpuMin.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuMin, mConfig.getCpuFreqEntries(), mConfig.getCpuFreqValues()));
        mCpuMin.setMulticore(true);
        mCpuMin.setisMaxFreq(false);

        mCpuGovernor.setEntries(mConfig.getCpuGovernors());
        mCpuGovernor.setValues(mConfig.getCpuGovernors());
        mCpuGovernor.setFilePath(Config.GOVERNOR_FILE);
        mCpuGovernor.setValue(mConfig.getCurrentGovernor());
        mCpuGovernor.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuGovernor, mConfig.getCpuGovernors(), mConfig.getCpuGovernors()));

        mCore1.setFilePath(Config.CORE_1_PATH);
        mCore1.setPositiveValue("1");
        mCore1.setNegativeValue("0");

        mCore2.setFilePath(Config.CORE_1_PATH);
        mCore2.setPositiveValue("1");
        mCore2.setNegativeValue("0");

        mCore3.setFilePath(Config.CORE_1_PATH);
        mCore3.setPositiveValue("1");
        mCore3.setNegativeValue("0");

        mMpdec.setPositiveValue("mpdecision start");
        mMpdec.setNegativeValue("mpdecision stop");
        mMpdec.setIsCustomCommand(true);


        govParamsPath = Config.GOVERNOR_FILES_DIR + mCpuGovernor.getValue();

        mAdvancedGovernor.hideBoot(true);
        mAdvancedHotplug.hideBoot(true);

        mAdvancedGovernor.setOnPreferenceClickListener(this);
        mAdvancedHotplug.setOnPreferenceClickListener(this);
        mMpdec.setOnPreferenceClickListener(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        
        boolean mpExists = Helpers.mpdecisionExists();
        if(!mpExists) {
            mCoreCategory.removePreference(mMpdec);
            showHideCores(true);
        }
        if(!Helpers.fileExists(Config.HOTPLUG_FOLDER)) {
           mAdvancedCategory.removePreference(mAdvancedHotplug); 
        }

    };
    
    @Override
    public void onLoadingFinished() {
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        showHideCores(prefs.getBoolean(Config.KEY_CPU_MPDEC+"_checkbox", true));

    }
    
    @Override
    public void onResume() {
        super.onResume();
        this.getListener().onComplete();
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        // TODO Auto-generated method stub
        if(pref == mAdvancedGovernor) {
            mUiHelpers.startActivity(
                    getActivity(), 
                    ParamsActivity.class, 
                    Helpers.getGovernorParamsDir(mConfig.getCpuGovernors()),
                    Config.EXTRA_FILES_DIR);
        }
        if(pref == mAdvancedHotplug) {
            mUiHelpers.startActivity(
                    getActivity(),
                    ParamsActivity.class,
                    Config.HOTPLUG_FOLDER,
                    Config.EXTRA_FILES_DIR);
        }
        if(pref == mMpdec) {
            boolean checked = mMpdec.isChecked();
            mMpdec.setChecked(checked ? false : true);
            showHideCores(mMpdec.isChecked());
        }
        return false;
    }

    private void showHideCores(boolean hide) {
        if(!hide) {
            mCoreCategory.addPreference(mCore1);
            mCoreCategory.addPreference(mCore2);
            mCoreCategory.addPreference(mCore3);
        }else {
            mCoreCategory.removePreference(mCore1);
            mCoreCategory.removePreference(mCore2);
            mCoreCategory.removePreference(mCore3);
        }
    }

}
