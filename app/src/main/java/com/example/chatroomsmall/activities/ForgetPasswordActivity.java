package com.example.chatroomsmall.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatroomsmall.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private FirebaseAuth mAuth;
    private Button btnReset, btnBack;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        addControls();
        addEvents();
    }

    private void addEvents() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                onClickResetPassword();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                onClickBack();
            }
        });
    }

    private void onClickBack() {
        startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
        progressDialog.dismiss();
        finish();
    }

    private void onClickResetPassword() {
        String strEmail = edtEmail.getText().toString().trim();
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Reset success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Reset fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addControls() {
        edtEmail = findViewById(R.id.edt_email_forgot);
        btnBack = findViewById(R.id.btn_back_forgot_password);
        btnReset = findViewById(R.id.btn_reset_pass);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("wait.....");
        mAuth = FirebaseAuth.getInstance();
    }
}