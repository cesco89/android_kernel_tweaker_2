package com.dsht.kerneltweaker;

import com.activeandroid.ActiveAndroid;

public class App extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        Config.getInstance().init(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

}
