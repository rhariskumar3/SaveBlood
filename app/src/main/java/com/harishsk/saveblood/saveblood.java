package com.harishsk.saveblood;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class saveblood extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
