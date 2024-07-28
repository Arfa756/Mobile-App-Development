package com.demo.furnitureapp;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseApp.initializeApp(this);
        FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
                    @Override
                    public void onComplete(Task<InstallationTokenResult> task) {
                        if (task.isSuccessful()) {
                            InstallationTokenResult result = task.getResult();
                            String token = result.getToken();
                            Log.d("Installations", "Installation auth token: " + token);
                        } else {
                            Log.e("Installations", "Unable to get Installation auth token");
                        }
                    }
                });

    }
}
