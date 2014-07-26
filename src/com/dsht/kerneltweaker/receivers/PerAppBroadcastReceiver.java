package com.dsht.kerneltweaker.receivers;

import com.dsht.kerneltweaker.Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PerAppBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(!intent.getAction().equals(Config.BROADCAST_INTENT) || intent.getExtras().getString(Config.BROADCAST_EXTRA_KEY) == null) {
            return;
        }
        Bundle b = intent.getExtras();
        String received = b.getString(Config.BROADCAST_EXTRA_KEY);
        Log.d("RECEIVER", received);
        
    }

}
