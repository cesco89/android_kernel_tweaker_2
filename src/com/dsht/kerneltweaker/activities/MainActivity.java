package com.dsht.kerneltweaker.activities;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.fragments.MenuPreferenceFragment;
import com.dsht.kerneltweaker.fragments.CpuStatsFragment;
import com.dsht.kerneltweaker.interfaces.OnCompleteListener;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.stericson.RootTools.RootTools;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class MainActivity extends FragmentActivity implements OnCompleteListener{

    public static SlidingMenu mMenu;
    private Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
        RootTools.isAccessGiven();

        mConfig = Config.getInstance();
        mConfig.load();

        View v = this.getLayoutInflater().inflate(R.layout.menu_layout, null, false);
        mMenu = new SlidingMenu(this);
        mMenu.setMode(SlidingMenu.LEFT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setShadowWidthRes(R.dimen.shadow_width);
        mMenu.setShadowDrawable(R.drawable.shadow_right);
        //mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mMenu.setBehindWidthRes(R.dimen.slidingmenu_offset);
        mMenu.setFadeDegree(0.50f);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mMenu.setMenu(v);


        if (savedInstanceState == null) {
            new UiHelpers(this).loadFragment(new CpuStatsFragment());
        }
        
        Fragment f = new MenuPreferenceFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
        .replace(R.id.menu_frame, f, Config.FRAGMENT_TAG)
        .commit();        

    }
    
    @Override
    public void onStart() {
      super.onStart();
      EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
      super.onStop();
      EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onComplete() {
        // TODO Auto-generated method stub
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mMenu.showContent();
            }
        };
        new Handler().postDelayed(r, 200);

    }

}
