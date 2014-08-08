package com.dsht.kerneltweaker.activities;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.database.DatabaseHelpers;
import com.dsht.kerneltweaker.interfaces.OnValueChangedListener;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.widgets.GenericPreference;
import com.dsht.kerneltweaker.widgets.ListPreference;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProfilePreferenceActivity extends PreferenceActivity implements OnValueChangedListener {

    private ListPreference mCpuMax;
    private ListPreference mCpuMin;
    private ListPreference mCpuGovernor;
    private Config mConfig;
    private UiHelpers mUiHelpers;

    private String newMaxFreq;
    private String newMinFreq;
    private String newGovernor;
    private String profileName;
    private boolean isEditing = false;
    private Resources res;
    private DatabaseHelpers mDb;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_profile);

        res = this.getResources();
        mConfig = Config.getInstance();
        mUiHelpers = new UiHelpers(this);
        mDb = new DatabaseHelpers();

        mCpuMax = (ListPreference) findPreference(Config.KEY_CPU_MAX_FREQ_PROFILE);
        mCpuMin = (ListPreference) findPreference(Config.KEY_CPU_MIN_FREQ_PROFILE);
        mCpuGovernor = (ListPreference) findPreference(Config.KEY_CPU_GOVERNOR_PROFILE);

        Bundle b = this.getIntent().getExtras();
        if(b != null) {
            isEditing = true;
            profileName = b.getString(Config.BUNDLE_PROFILE_NAME);
            newMaxFreq = b.getString(Config.BUNDLE_PROFILE_MAX);
            newMinFreq = b.getString(Config.BUNDLE_PROFILE_MIN);
            newGovernor = b.getString(Config.BUNDLE_PROFILE_GOVERNOR);

            this.getActionBar().setSubtitle(String.format(res.getString(R.string.ab_edit_template), profileName));
        }else{
            newMaxFreq = mConfig.getCurrentMaxFreq();
            newMinFreq = mConfig.getCurrentMinFreq();
            newGovernor = mConfig.getCurrentGovernor();
        }

        mCpuMax.setSummary((Integer.parseInt(newMaxFreq)/1000)+ " Mhz");
        mCpuMin.setSummary((Integer.parseInt(newMinFreq)/1000)+ " Mhz");
        mCpuGovernor.setSummary(newGovernor);
        
        mCpuMax.hideBoot(true);
        mCpuMin.hideBoot(true);
        mCpuGovernor.hideBoot(true);

        mCpuMax.setFilePath(Config.MAX_FREQ_FILE);
        mCpuMax.setValue(newMaxFreq);
        mCpuMax.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuMax, mConfig.getCpuFreqEntries(), mConfig.getCpuFreqValues()));
        mCpuMax.setMustApplyValues(false);

        mCpuMin.setFilePath(Config.MIN_FREQ_FILE);
        mCpuMin.setValue(newMinFreq);
        mCpuMin.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuMin, mConfig.getCpuFreqEntries(), mConfig.getCpuFreqValues()));
        mCpuMin.setMulticore(true);
        mCpuMin.setIsMaxFreq(false);
        mCpuMin.setMustApplyValues(false);

        mCpuGovernor.setFilePath(Config.GOVERNOR_FILE);
        mCpuGovernor.setValue(newGovernor);
        mCpuGovernor.setDialog(mUiHelpers.buildListPreferenceDialog(mCpuGovernor, mConfig.getCpuGovernors(), mConfig.getCpuGovernors()));
        mCpuGovernor.setMustApplyValues(false);

        mCpuMax.setOnValueChangedListener(this);
        mCpuMin.setOnValueChangedListener(this);
        mCpuGovernor.setOnValueChangedListener(this);

        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { 
        this.getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
        case R.id.action_save:
            if(!isEditing) {
                mUiHelpers.buildSaveProfileDialog(newMaxFreq, newMinFreq, newGovernor).show();
            }else{
                mDb.saveProfile(profileName, newMaxFreq, newMinFreq, newGovernor);
            }
            break;
        case android.R.id.home:
            mUiHelpers.finishActivity(this);
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mUiHelpers.finishActivity(this);
    }
    
    @Override
    public void onValueChanged(GenericPreference pref, String value) {
        // TODO Auto-generated method stub
        String key = pref.getKey();
        if(key.equals(Config.KEY_CPU_MAX_FREQ_PROFILE)) {
            newMaxFreq = value;
        }else if(key.equals(Config.KEY_CPU_MIN_FREQ_PROFILE)) {
            newMinFreq = value;
        }else if(key.equals(Config.KEY_CPU_GOVERNOR_PROFILE)) {
            newGovernor = value;
        }

    }

}
