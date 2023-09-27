package com.example.chatroomsmall.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatroomsmall.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ImageUpLoadPrevious extends AppCompatActivity {


    private FloatingActionButton btnBack;
    private ImageView imageUpload;
    private ImageButton imgBtnCam, imBtnSend;
    private StorageReference storageReference, imagePath;
    private String imageString;
    private FirebaseFirestore firebaseFirestore;
    private EditText edtMessage;
    private Uri imageUri;
    private ProgressDialog progressDialog;


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
        edtMessage = findViewById(R.id.edt_input_message_previous);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Upload.....");

        Bundle bundle = getIntent().getExtras();
        // String image = bundle.getString("image_bitmap");

        imageUri = getIntent().getParcelableExtra("image_bitmap");
       // Bitmap bitmap = getIntent().getParcelableExtra("image_bitmap");

      //  Log.e("imageUri", imageUri.toString());
       // imageString = String.valueOf(bitmap);
        imageUpload.setImageURI(imageUri);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("CHAT");
    }

    private void addEvents() {
        imBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
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

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String messageId = format.format(date);
        storageReference = FirebaseStorage.getInstance().getReference().child("chat_image");
        imagePath = storageReference.child(messageId + ".jpg");

        imagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // lay duong dan url ma ta tai anh len storage
                    Log.e("imagePath", imagePath.getDownloadUrl().toString());

                    imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            addMessageToFirebaseStore(uri, messageId);
                        }
                    });

                }
            }
        });
    }

    private void addMessageToFirebaseStore(Uri uri, String messageId) {

        HashMap<String, Object> messageObj = new HashMap<>();
      //  messageObj.put("message", message);
      //  messageObj.put("user_name", mUser.getDisplayName());
        //  messageObj.put("timestamp",  new Date().getTime());
        messageObj.put("messageId", messageId);
        messageObj.put("chat_image", uri.toString());
        //messageObj.put("user_image_url", user_image_url);

        firebaseFirestore.document(messageId).set(messageObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ImageUpLoadPrevious.this, "Xong", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                    finish();
                }
                 else {
                    Toast.makeText(ImageUpLoadPrevious.this, "Loi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}