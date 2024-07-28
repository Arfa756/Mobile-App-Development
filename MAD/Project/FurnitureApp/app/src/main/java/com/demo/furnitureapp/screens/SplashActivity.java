package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.PreferencesUtil.getLoginAsAdmin;
import static com.demo.furnitureapp.utility.PreferencesUtil.getStayLogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.furnitureapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mBinding;
    private Handler nextActivityHandler;
    private Runnable nextActivityRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the binding
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Initialize the handler
        nextActivityHandler = new Handler(Looper.getMainLooper());

    }

    private void startHandler() {
        nextActivityRunnable = new Runnable() {
            @Override
            public void run() {
                if (getStayLogin(SplashActivity.this)) {
                    if (getLoginAsAdmin(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, UserActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        nextActivityHandler.postDelayed(nextActivityRunnable, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();
    }

    @Override
    protected void onPause() {
        if (nextActivityRunnable != null) {
            nextActivityHandler.removeCallbacks(nextActivityRunnable);
        }
        super.onPause();
    }
}