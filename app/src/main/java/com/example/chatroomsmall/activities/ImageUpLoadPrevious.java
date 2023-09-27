package com.example.chatroomsmall.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chatroomsmall.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ImageUpLoadPrevious extends AppCompatActivity {


    private FloatingActionButton btnBack;
    private ImageView imageUpload;
    private ImageButton imgBtnCam, imBtnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_up_load_previous);

        addControls();
        addEvents();
    }

    private void addControls() {
        imageUpload = findViewById(R.id.image_cam_previous);
        btnBack = findViewById(R.id.btn_back_imageUpload);
        imgBtnCam = findViewById(R.id.imgbtn_camera_previous);
        imBtnSend = findViewById(R.id.imgbtn_send_previous);

        Bundle bundle = getIntent().getExtras();
        // String image = bundle.getString("image_bitmap");
        Bitmap bitmap = getIntent().getParcelableExtra("image_bitmap");
        Log.e("BITMAP", bitmap.toString());
        imageUpload.setImageBitmap(bitmap);
    }

    private void addEvents() {
        imBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSend();
            }
        });

        imgBtnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCamera();
            }
        });
    }

    private void onClickCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle data = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) data.get("data");
                        if (bitmap != null) {
                            imageUpload.setImageBitmap(bitmap);
                        }
                    }
                }
            });

    private void onClickSend() {
    }
}