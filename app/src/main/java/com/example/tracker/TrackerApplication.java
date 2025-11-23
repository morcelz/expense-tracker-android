package com.example.tracker;

import android.app.Application;

public class TrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Realm removed. Retrofit is stateless/singleton.
    }
}
