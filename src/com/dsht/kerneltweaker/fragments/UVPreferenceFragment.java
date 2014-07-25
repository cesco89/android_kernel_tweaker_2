package com.dsht.kerneltweaker.fragments;

import java.util.ArrayList;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.R;
import com.dsht.kerneltweaker.interfaces.OnLoadingFinishedListener;
import com.dsht.kerneltweaker.interfaces.OnValueChangedListener;
import com.dsht.kerneltweaker.utils.CMDHelpers;
import com.dsht.kerneltweaker.utils.Helpers;
import com.dsht.kerneltweaker.utils.UiHelpers;
import com.dsht.kerneltweaker.utils.UvItem;
import com.dsht.kerneltweaker.widgets.EditPreference;
import com.dsht.kerneltweaker.widgets.GenericPreference;
import com.dsht.kerneltweaker.widgets.ObservablePreferenceFragment;
import com.dsht.kerneltweaker.widgets.QuickReturnListView;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UVPreferenceFragment extends ObservablePreferenceFragment implements OnClickListener, OnScrollListener, OnValueChangedListener, OnLoadingFinishedListener{

    private PreferenceScreen mRoot;
    private UiHelpers mUiHelpers;
    private CMDHelpers mCmd;
    private ArrayList<String> values = new ArrayList<String>();
    private ArrayList<UvItem> uvItems;
    private int buttonCount = 0;
    private LinearLayout mButtonsLayout;
    private TextView mMillivoltText;

    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;
    private static OnGlobalLayoutListener mObserver;

    private int mQuickReturnHeight;

    private static QuickReturnListView mList;
    private LinearLayout mMenu;
    private Config mConfig;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs_uv);
        mUiHelpers = new UiHelpers(getActivity());
        mCmd = new CMDHelpers();
        mConfig = Config.getInstance();
        mRoot = (PreferenceScreen) findPreference(Config.KEY_ROOT);
        uvItems = Helpers.getUvTable(values, mConfig.getUvPath());
        if(uvItems != null) {
            mUiHelpers.createUvPreferences(mRoot, uvItems, mConfig.getUvPath(), this, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.uv_layout, container, false);
        Button mPlus = (Button) v.findViewById(R.id.button_plus);
        Button mMinus = (Button) v.findViewById(R.id.button_minus);
        Button mApply = (Button) v.findViewById(R.id.btn_apply);
        Button mCancel = (Button) v.findViewById(R.id.btn_cancel);
        mButtonsLayout = (LinearLayout) v.findViewById(R.id.apply_layout);
        mMillivoltText = (TextView) v.findViewById(R.id.text_mv);
        mList = (QuickReturnListView) v.findViewById(android.R.id.list);
        mMenu = (LinearLayout) v.findViewById(R.id.menu);

        mPlus.setOnClickListener(this);
        mMinus.setOnClickListener(this);
        mApply.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()) {
        case R.id.button_minus:
            buttonCount-=1;
            applyBatchValues(false);
            break;
        case R.id.button_plus:
            buttonCount+=1;
            applyBatchValues(true);
            break;
        case R.id.btn_apply:
            mCmd.setUndervolt(uvItems, values, Helpers.checkUVTableTypeInt(), mConfig.getUvPath());
            showHidePanel();
            break;
        case R.id.btn_cancel:
            restoreValues((buttonCount*25));
            break;
        }
        if(buttonCount > 0) {
            mMillivoltText.setText("+"+(buttonCount*25)+"mV");
        }else if(buttonCount <= 0) {
            mMillivoltText.setText((buttonCount*25)+"mV"); 
        }

    }
    
    @Override
    public void onLoadingFinished() {
        // TODO Auto-generated method stub
        setUpList();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {

        mScrollY = 0;
        int translationY = 0;

        if (mList.scrollYIsComputed()) {
            mScrollY = mList.getComputedScrollY();
        }

        int rawY = mScrollY;

        switch (mState) {
        case STATE_OFFSCREEN:
            if (rawY >= mMinRawY) {
                mMinRawY = rawY;
            } else {
                mState = STATE_RETURNING;
            }
            translationY = rawY;
            break;

        case STATE_ONSCREEN:
            if (rawY > mQuickReturnHeight) {
                mState = STATE_OFFSCREEN;
                mMinRawY = rawY;
            }
            translationY = rawY;
            break;

        case STATE_RETURNING:

            translationY = (rawY - mMinRawY) + mQuickReturnHeight;

            System.out.println(translationY);
            if (translationY < 0) {
                translationY = 0;
                mMinRawY = rawY + mQuickReturnHeight;
            }

            if (rawY == 0) {
                mState = STATE_ONSCREEN;
                translationY = 0;
            }

            if (translationY > mQuickReturnHeight) {
                mState = STATE_OFFSCREEN;
                mMinRawY = rawY;
            }
            break;
        }

        /** this can be used if the build is below honeycomb **/
        mMenu.setTranslationX(translationY);

    }
    
    private void setUpList() {
        mObserver = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mQuickReturnHeight = mMenu.getWidth();
                mList.computeScrollY();
            }
        };
        mList.getViewTreeObserver().addOnGlobalLayoutListener(mObserver);
        mList.setOnScrollListener(this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
               UVPreferenceFragment.this.getListener().onComplete();
            }

        }, 200);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onValueChanged(GenericPreference pref, String value) {
        // TODO Auto-generated method stub
        Log.d("VALUE "+pref.getPosition(), values.get(pref.getPosition()));
        String oldValue = values.get(pref.getPosition());
        values.set(pref.getPosition(), value);
        UvItem mItem = uvItems.get(pref.getPosition());
        mItem.setValue(value);
        uvItems.set(pref.getPosition(), mItem);
        Log.d("NEW VALUE "+pref.getPosition(), values.get(pref.getPosition()));
        if(!value.equals(oldValue)) {
            int tableType = Helpers.checkUVTableTypeInt();
            Log.d("TABLETYPE = "+tableType, "-----pushing values-----");
            mCmd.setUndervolt(uvItems, values, tableType, mConfig.getUvPath());
        }

    }

    @SuppressWarnings("deprecation")
    public static void removeListener() {
        mList.getViewTreeObserver().removeGlobalOnLayoutListener(mObserver);
    }

    public static OnGlobalLayoutListener getLayoutListener() {
        return mObserver;
    }

    private void applyBatchValues(final boolean plus) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                PreferenceScreen mScreen = UVPreferenceFragment.this.getPreferenceScreen();
                int prefsIndex = mScreen.getPreferenceCount();
                if(plus) {
                    for(int i = 0; i<prefsIndex; i++) {
                        EditPreference pref = (EditPreference)mScreen.getPreference(i);
                        pref.setSummaryPlus(25);
                        String curValue = values.get(i);
                        String newValue = (Integer.parseInt(curValue)+25)+"";
                        values.set(i, newValue);
                        Log.d("NEWVALUE+", newValue);
                        if(i == prefsIndex-1) {
                            showHidePanel();
                        }
                    }
                }else {
                    for(int i = 0; i<prefsIndex; i++) {
                        EditPreference pref = (EditPreference)mScreen.getPreference(i);
                        pref.setSummaryMinus(25);
                        String curValue = values.get(i);
                        String newValue = (Integer.parseInt(curValue)-25)+"";
                        values.set(i, newValue);
                        Log.d("NEWVALUE-", newValue);
                        if(i == prefsIndex-1) {
                            showHidePanel();
                        }
                    }
                }
            }
        };
        new Handler().post(r);
    }

    private void restoreValues(final int how) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                PreferenceScreen mScreen = UVPreferenceFragment.this.getPreferenceScreen();
                int prefsIndex = mScreen.getPreferenceCount();
                for(int i = 0; i<prefsIndex; i++) {
                    EditPreference pref = (EditPreference)mScreen.getPreference(i);
                    if(how >= 0 ){
                        pref.setSummaryMinus(how);
                    }else if(how <= 0) {
                        pref.setSummaryPlus(-how);
                    }
                    String curValue = values.get(i);
                    String newValue = (Integer.parseInt(curValue)+25)+"";
                    values.set(i, newValue);
                    if(i == prefsIndex-1) {
                        mButtonsLayout.setVisibility(View.GONE);

                    }
                };
            }
        };
        new Handler().post(r);
        buttonCount = 0;
    }
    private void showHidePanel() {
        if(buttonCount != 0) {
            if(mButtonsLayout.getVisibility() == View.GONE) {
                mButtonsLayout.setVisibility(View.VISIBLE);
            }
        }else {
            if(mButtonsLayout.getVisibility() == View.VISIBLE) {
                mButtonsLayout.setVisibility(View.GONE);
            }
        }
    }
    
    private void showApply() {
        if(mButtonsLayout.getVisibility() == View.GONE) {
            mButtonsLayout.setVisibility(View.VISIBLE);
        }
    }

}
