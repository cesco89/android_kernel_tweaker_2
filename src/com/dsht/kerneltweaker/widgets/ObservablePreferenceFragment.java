package com.dsht.kerneltweaker.widgets;

import com.dsht.kerneltweaker.interfaces.OnCompleteListener;

import android.app.Activity;
import android.preference.PreferenceFragment;

public class ObservablePreferenceFragment extends PreferenceFragment{

    private OnCompleteListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
    
    public OnCompleteListener getListener() {
        return this.mListener;
    }

}
