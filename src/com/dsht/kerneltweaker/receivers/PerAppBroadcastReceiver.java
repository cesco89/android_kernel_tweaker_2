package com.dsht.kerneltweaker.receivers;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.utils.CMDHelpers;
import com.dsht.kerneltweaker.utils.Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class PerAppBroadcastReceiver extends BroadcastReceiver{

    private String curPackage;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(!intent.getAction().equals(Config.BROADCAST_INTENT) || intent.getExtras().getString(Config.BROADCAST_EXTRA_KEY) == null) {
            return;
        }
        String received = intent.getExtras().getString(Config.BROADCAST_EXTRA_KEY);
        if(!received.equals(curPackage) && !received.equals("com.android.systemui")){
            Log.d("RECEIVER", "new package received");
            curPackage = received;
            Log.d("RECEIVER", "setting profile values");
            Helpers.applyProfile(context, curPackage);
        }else{
            Log.d("RECEIVER", "nothing to do");
        }
        received = null;

    }

}
