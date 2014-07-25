package com.dsht.kerneltweaker.fragments;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.CheckBoxPreference;
import com.dsht.kerneltweaker.widgets.GreenPreferenceCategory;
import com.dsht.kerneltweaker.widgets.ListPreference;
import com.dsht.kerneltweaker.widgets.ObservablePreferenceFragment;
import com.stericson.RootTools.RootTools;

import android.os.Bundle;

public class KernelPreferenceFragment extends ObservablePreferenceFragment {

    private CheckBoxPreference mLogcat;
    private CheckBoxPreference mFsync;
    private ListPreference mScheduler;
    private ListPreference mReadAhead;
    private CheckBoxPreference mFcharge;
    private ListPreference mTcp;
    private GreenPreferenceCategory mIoCategory, mPowerCategory, mNetCategory;

    private UiHelpers mUiHelpers;

    private String TAG = this.getClass().getName();
    private String mLogcatValue;
    private String mFsyncValue;
    private String mCurrentScheduler;
    private String[] mAvailableSchedGovs;
    private String[] mReadAheadEntries;
    private String[] mReadAheadValues;
    private String mCurrentReadAhead;
    private String mFchargeValue;
    private String mTcpCurrent;
    private String[] mTcpAvailable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_kernel);
        mUiHelpers = new UiHelpers(getActivity());

        mLogcatValue = Helpers.readFileViaShell(Config.LOGCAT, false);
        mCurrentScheduler = Helpers.getCurrentScheduler();
        mAvailableSchedGovs = Helpers.getAvailableSchedulers();
        mReadAheadEntries = Helpers.readAheadEntries();
        mReadAheadValues = Helpers.readAheadValues();
        mCurrentReadAhead = Helpers.readFileViaShell(Config.READ_AHEAD_FILE, false);

        //Log.d(TAG, mLogcatValue);
        //Log.d(TAG, mFsyncValue);

        mIoCategory = (GreenPreferenceCategory) findPreference(Config.KEY_KERNEL_CATEGORY_IO);
        mPowerCategory = (GreenPreferenceCategory) findPreference(Config.KEY_KERNEL_CATEGORY_POWER);
        mNetCategory = (GreenPreferenceCategory) findPreference(Config.KEY_KERNEL_CATEGORY_NET);

        mLogcat = (CheckBoxPreference) findPreference(Config.KEY_LOGCAT);
        mFsync = (CheckBoxPreference) findPreference(Config.KEY_FSYNC);
        mScheduler = (ListPreference) findPreference(Config.KEY_SCHED);
        mReadAhead = (ListPreference) findPreference(Config.KEY_READ_AHEAD);
        mFcharge = (CheckBoxPreference) findPreference(Config.KEY_FCHARGE);
        mTcp = (ListPreference) findPreference(Config.KEY_TCP);

        mLogcat.setFilePath(Config.LOGCAT);
        mLogcat.setPositiveValue("1");
        mLogcat.setNegativeValue("0");
        mLogcat.setValue(mLogcatValue);
        mLogcat.setChecked(Integer.parseInt(mLogcatValue) == 1 ? true : false);

        if(Helpers.fileExists(Config.FSYNC_FILE)) {
            mFsyncValue = Helpers.readFileViaShell(Config.FSYNC_FILE, false);
            mFsync.setFilePath(Config.FSYNC_FILE);
            mFsync.setPositiveValue("Y");
            mFsync.setNegativeValue("N");
            mFsync.setChecked(mFsyncValue.equals("Y") ? true : false);
        }else{
            mIoCategory.removePreference(mFsync);
            if(mIoCategory.getPreferenceCount() == 0) {
                this.getPreferenceScreen().removePreference(mIoCategory);
            }
        }

        mScheduler.setSummary(mCurrentScheduler);
        mScheduler.setEntries(mAvailableSchedGovs);
        mScheduler.setValues(mAvailableSchedGovs);
        mScheduler.setValue(mCurrentScheduler);
        mScheduler.setFilePath(Config.SCHEDULER_FILE);
        mScheduler.setDialog(mUiHelpers.buildListPreferenceDialog(mScheduler, mAvailableSchedGovs, mAvailableSchedGovs));

        mReadAhead.setSummary(mCurrentReadAhead+" KB");
        mReadAhead.setEntries(mReadAheadEntries);
        mReadAhead.setValues(mReadAheadValues);
        mReadAhead.setValue(mCurrentReadAhead);
        mReadAhead.setFilePath(Config.READ_AHEAD_FILE);
        mReadAhead.setDialog(mUiHelpers.buildListPreferenceDialog(mReadAhead, mReadAheadEntries, mReadAheadValues));


        if(Helpers.fileExists(Config.FCHARGE_FILE)) {
            mFchargeValue = Helpers.readFileViaShell(Config.FCHARGE_FILE, false);
            mFcharge.setFilePath(Config.FCHARGE_FILE);
            mFcharge.setPositiveValue("1");
            mFcharge.setNegativeValue("0");
            mFcharge.setChecked(Integer.parseInt(mFchargeValue) == 1 ? true : false);
        }else{
            mPowerCategory.removePreference(mFcharge);
            if(mPowerCategory.getPreferenceCount() == 0) {
                this.getPreferenceScreen().removePreference(mPowerCategory);
            }
        }

        if(RootTools.isBusyboxAvailable()) {
            mTcpCurrent = Helpers.getCurrentTCP();
            mTcpAvailable = Helpers.getAvailableTCP();
            mTcp.setSummary(mTcpCurrent);
            mTcp.setEntries(mTcpAvailable);
            mTcp.setValues(mTcpAvailable);
            mTcp.setValue(mTcpCurrent);
            mTcp.setFilePath(Config.TCP_COMMAND_REGEX);
            mTcp.setIsCustomCommand(true);
            mTcp.setDialog(mUiHelpers.buildListPreferenceDialog(mTcp, mTcpAvailable, mTcpAvailable));
        }else{
            mNetCategory.removePreference(mTcp);
            if(mNetCategory.getPreferenceCount() == 0) {
                this.getPreferenceScreen().removePreference(mNetCategory);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        this.getListener().onComplete();
    }

}
