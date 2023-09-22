package com.example.chatroomsmall.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatroomsmall.MainActivity;
import com.example.chatroomsmall.R;

public class StartChatActivity extends AppCompatActivity {

    private CardView cvEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        addControls();
        addEvents();
    }

    private void addEvents() {
        cvEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEnter();
            }
        });
    }

    private void onClickEnter() {
        startActivity(new Intent(StartChatActivity.this, MainActivity.class));
        finish();
    }

    private void addControls() {
        cvEnter = findViewById(R.id.cv_enter);
    }
}