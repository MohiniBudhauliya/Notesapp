package com.example.mohini.notes.activities.database;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by mohini on 5/2/18.
 */

public class DbflowApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        Timber.plant(new Timber.DebugTree());
        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }
}
