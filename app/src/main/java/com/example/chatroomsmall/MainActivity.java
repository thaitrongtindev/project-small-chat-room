package com.example.chatroomsmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private ImageButton imageBtnCamera, imageBtnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String token = task.getResult();
                Log.e("TOKEN", token.toString() );
            }
        }) ;
        addControls();
        addEvents();
    }

    private void addEvents() {
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rcv_content_chat);
        imageBtnCamera = findViewById(R.id.imgbtn_camera);
        imageBtnSend = findViewById(R.id.imgbtn_send);

        mAuth = FirebaseAuth.getInstance();
        // firebase firestore
        db = FirebaseFirestore.getInstance();

        /// tạo tham chiếu đến collection trong firestore
        CollectionReference collectionReference = db.collection("CHAT");
        Query query = collectionReference.orderBy("timestamp", Query.Direction.DESCENDING);
        // khỏi tạo Firestore Recyclerview

        FirestoreRecyclerOptions<ChatModel> options = new FirestoreRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel.class).build();
        // chuyển đôi dữ liệu trên firestore thành đối tượng ChatModel
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // initial adapter
        chatAdapter = new ChatAdapter(options);
        recyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();




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