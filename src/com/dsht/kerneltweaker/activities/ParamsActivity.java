package com.dsht.kerneltweaker.activities;

import java.io.File;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.fragments.ParamsPreferenceFragment;
import com.dsht.kerneltweaker.interfaces.OnCompleteListener;
import com.dsht.kerneltweaker.utils.UiHelpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class ParamsActivity extends Activity implements OnCompleteListener {

    private Fragment mFragment;
    private String mFilesDir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mFilesDir = extras.getString(Config.EXTRA_FILES_DIR);
            if(mFilesDir != null) {
                Log.d("PASSED FILE", mFilesDir);
                loadFragment();
            }
        }

    }


    private void loadFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment frag = (ParamsPreferenceFragment) fm.findFragmentByTag("fragment");
        if(frag == null) {
            FragmentManager fragManager = getFragmentManager();
            fragManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.container, ParamsPreferenceFragment.newInstance(mFilesDir), "fragment").commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
        case android.R.id.home:
            new UiHelpers(this).finishActivity(this);
            break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new UiHelpers(this).finishActivity(this);
    }


    @Override
    public void onComplete() {
        // TODO Auto-generated method stub
        
    }

}
