package com.dsht.kerneltweaker.services;

import com.dsht.kerneltweaker.Config;
import com.dsht.kerneltweaker.receivers.PerAppBroadcastReceiver;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class PerAppService extends AccessibilityService {
    
    private PerAppBroadcastReceiver mReceiver;
    
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v("PER-APP-SERVICE", "onServiceConnected");
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = AccessibilityServiceInfo.FEEDBACK_BRAILLE;
        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        this.setServiceInfo(accessibilityServiceInfo);
        
        mReceiver = new PerAppBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter(Config.BROADCAST_INTENT);
        this.registerReceiver(mReceiver, mFilter);
    }
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent arg0) {
        // TODO Auto-generated method stub
        String packageName = arg0.getPackageName().toString();
        Log.d("SERVICE", packageName);
        sendIntent(packageName);
        
    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }
    
    private void sendIntent(String packageName) {
        Intent i = new Intent(Config.BROADCAST_INTENT);
        Bundle extras = new Bundle();
        extras.putString(Config.BROADCAST_EXTRA_KEY, packageName);
        i.putExtras(extras);
        this.sendBroadcast(i);
    }

}
