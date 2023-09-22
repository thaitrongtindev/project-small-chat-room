package com.example.chatroomsmall.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatroomsmall.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long TIME_OUT = 5000;
    private Animation topAnim, bottomAnim;
    private ImageView imageView;
    private TextView logo, slogan;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        mAuth = FirebaseAuth.getInstance();
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // hooks
        imageView = findViewById(R.id.imageView);
        logo = findViewById(R.id.tv_logo);
        slogan = findViewById(R.id.tv_slogon);

        imageView.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, TIME_OUT);
    }

    private void nextActivity() {
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(SplashScreenActivity.this, StartChatActivity.class));
        }
    }
}