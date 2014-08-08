package com.dsht.kerneltweaker.fragments;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.activities.MainActivity;
import com.dsht.kerneltweaker.adapters.PerAppPagerAdapter;
import com.dsht.kerneltweaker.utils.Helpers;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class PerAppFragment extends Fragment implements OnClickListener {

    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;
    private PerAppPagerAdapter mAdapter;
    private Config mConfig;
    private Resources res;
    private FrameLayout mServiceSettings;
    private View mHelpLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = Config.getInstance();
        res = getActivity().getResources();
        
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.per_app_main, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        mTabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        mPager = (ViewPager) v.findViewById(R.id.pager);
        mServiceSettings = (FrameLayout) v.findViewById(R.id.open_accessibility);
        mHelpLayout = (View) v.findViewById(R.id.help_layout);
        mServiceSettings.setOnClickListener(this);
        
        mAdapter = new PerAppPagerAdapter(this.getChildFragmentManager(), res.getStringArray(R.array.per_app_titles));
        mPager.setAdapter(mAdapter);
        mTabs.setViewPager(mPager);
        mTabs.setIndicatorColor(res.getColor(R.color.mint_green));
        mTabs.setTextColor(Color.WHITE);
        mTabs.setDividerColor(res.getColor(R.color.content_bg));

        mTabs.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                ((MainActivity)getActivity()).onPageChanged(arg0);
            }

        });
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        ((MainActivity)getActivity()).onComplete();
        if(Helpers.isAccessibilityEnabled(getActivity(), Config.PERAPP_SERVICE_ID)) {
           mHelpLayout.setVisibility(View.GONE); 
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).onPagerFragmentDestroied();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch(arg0.getId()) {
        case R.id.open_accessibility:
            getActivity().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            break;
        }
    }

}
