package com.example.chatroomsmall;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroomsmall.activities.ImageUpLoadPrevious;
import com.example.chatroomsmall.activities.LoginActivity;
import com.example.chatroomsmall.adapter.ChatAdapter;
import com.example.chatroomsmall.model.ChatModel;
import com.facebook.login.LoginManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private ImageButton imageBtnCamera, imageBtnSend;
    private EditText edtMessage;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                Log.e("TOKEN", token.toString());
            }
        });
        addControls();
        addEvents();
    }

    private void addEvents() {
        imageBtnSend.setOnClickListener(view -> {
            onClickSendMessage();
        });

        imageBtnCamera.setOnClickListener(view -> {
            onClickCamera();
        });
    }

    private void onClickCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            return;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(cameraIntent);

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // nhận dữ liệu
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");

                            // Chuyển ảnh sang activity mới
                            if (imageBitmap != null) {
                                Intent intent = new Intent(MainActivity.this, ImageUpLoadPrevious.class);
                                intent.putExtra("image_bitmap", imageBitmap);
                                startActivity(intent);
                            }
                        }
                    }

                }
            }
    );

    private void onClickSendMessage() {
        String strMessage = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage) == false) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String messageId = format.format(date);

            // get image account
            String user_image_url = "";
            Uri photoUri = mUser.getPhotoUrl();
            Log.e("PhotoUri", photoUri.toString());

            if (photoUri != null) {
                user_image_url = photoUri.toString();
                //   Toast.makeText(this, user_image_url.toString(), Toast.LENGTH_SHORT).show();

                Log.e("URL IAMGE", user_image_url.toString());
            }

            HashMap<String, String> messageObj = new HashMap<>();
            messageObj.put("message", strMessage);
            messageObj.put("user_name", mUser.getDisplayName());
            messageObj.put("timestamp", FieldValue.serverTimestamp().toString());
            messageObj.put("messageId", messageId);
            messageObj.put("user_image_url", user_image_url);


            collectionReference.document(messageId).set(messageObj)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Send Mesgage", Toast.LENGTH_SHORT).show();
                                edtMessage.setText("");
                            }
                        }
                    });
        }
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rcv_content_chat);
        imageBtnCamera = findViewById(R.id.imgbtn_camera);
        imageBtnSend = findViewById(R.id.imgbtn_send);
        edtMessage = findViewById(R.id.edt_input_message);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // firebase firestore
        db = FirebaseFirestore.getInstance();

        /// tạo tham chiếu đến collection trong firestore
        collectionReference = db.collection("CHAT");
        Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING);
        // khỏi tạo Firestore Recyclerview

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class).build();
        // chuyển đôi dữ liệu trên firestore thành đối tượng ChatModel
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        // initial adapter
        chatAdapter = new ChatAdapter(options);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.startListening();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}