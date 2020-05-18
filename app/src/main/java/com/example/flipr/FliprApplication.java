package com.example.flipr;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FliprApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }
}
