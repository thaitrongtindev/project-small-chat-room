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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatroomsmall.MainActivity;
import com.example.chatroomsmall.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
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
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private CollectionReference collectionReference;


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

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        // String image = bundle.getString("image_bitmap");

        imageUri = getIntent().getParcelableExtra("image_bitmap");
       // Bitmap bitmap = getIntent().getParcelableExtra("image_bitmap");

      //  Log.e("imageUri", imageUri.toString());
       // imageString = String.valueOf(bitmap);
        imageUpload.setImageURI(imageUri);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("CHAT");

    }

    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImageUpLoadPrevious.this, MainActivity.class));
                finish();
            }
        });
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
                            Log.e("URI_IMAGE_SUCCEESS", uri.toString() );
                            addMessageToFirebaseStore(uri, messageId);
                        }
                    });

                }
            }
        });
    }

    private void addMessageToFirebaseStore(Uri uri, String messageId) {

        mUser = mAuth.getCurrentUser();
        Log.e("URI ADD MESSAGE", uri.toString());
        String message = edtMessage.getText().toString();

//        if (!TextUtils.isEmpty(message)) {
//            message ="\uD83D\uDCF7";
//            /*
//            Nếu trống, bạn gán cho biến message một emoji hình máy ảnh để biểu thị
//             rằng tin nhắn là một hình ảnh. Emoji này sẽ được hiển thị thay cho văn bản trống.
//             */
//        }

        if (TextUtils.isEmpty(message)) {
            message ="\uD83D\uDCF7";
            /*
            Nếu trống, bạn gán cho biến message một emoji hình máy ảnh để biểu thị
             rằng tin nhắn là một hình ảnh. Emoji này sẽ được hiển thị thay cho văn bản trống.
             */
        }

        // getting usser image form google account
        String user_image_url = "";
        Uri photoUri = mUser.getPhotoUrl();
        //         Log.e("TAG", "addSendMess: " + photoUri.toString());
        String originalUrl = "s96-c/photo.jpg";
        String resizeImageUrl = "s400-c/photo/jpg";
        if (photoUri != null) {
            String photoPath = photoUri.toString();
            user_image_url = photoPath.replace(originalUrl, resizeImageUrl);
            // Log.e("URL IAMGE", "addSendMess: " + user_image_url.toString());
        }

        HashMap<String, Object> messageObj = new HashMap<>();
        messageObj.put("message", message);
        messageObj.put("user_name", mUser.getDisplayName());
        messageObj.put("timestamp", FieldValue.serverTimestamp().toString());
        messageObj.put("messageId", messageId);
        messageObj.put("chat_image", uri.toString());
        messageObj.put("user_image_url", user_image_url);

        collectionReference.document(messageId).set(messageObj).addOnCompleteListener(new OnCompleteListener<Void>() {
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