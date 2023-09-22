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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatroomsmall.MainActivity;
import com.example.chatroomsmall.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin, btnLoginGoogle, btnLoginFacebook;
    private EditText edtPassword, edtEmail;
    private TextView tvRegister, tcForgotPassword;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;

    private GoogleSignInClient signInClient; // doi tuong nay dc sd
    // tuong tac voi service google sign in. bao roi viec dang nhap



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControls();
        addEvents();

        // gui yeu cau xin phep login gooogle
        createRequestLoginGoolge();

    }

    private void createRequestLoginGoolge() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

    }

    private void addEvents() {
        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xác định trạng thái hiện tại của mật khẩu và cập nhật nó
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    // Hiển thị mật khẩu
                    edtPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);

                } else {
                    // Ẩn mật khẩu
                    edtPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickResgiter();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
               onClickLogin();
            }
        });

        tcForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickForgotPassword();
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                onClickLoginGoogle();
            }
        });
        
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLoginFacebook();
            }
        });
    }

    private void onClickLoginFacebook() {
    }

    private void onClickLoginGoogle() {
        // open UI login google
        Intent intent = signInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    //get result retrun form login in form login
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data =result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);// lấy data từ
                        //thông tin đăng nhập từ intent data
                        try {
                            // lấy kết quả của việc đăng nhập
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            // có kết quả thì đem đi xac thức
                            auth(account.getIdToken());// xác thực người dùng
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void auth(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        // authcredential: đại diện thông tin xác thực từ người dùng
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    userProfile();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userProfile() {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }


    private void onClickForgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        finish();
    }

    private void onClickLogin() {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    // login susscess
                                    startActivity(new Intent(LoginActivity.this, StartChatActivity.class));
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

    }
    private void onClickResgiter() {
        startActivity(new Intent(LoginActivity.this, RegisterAccountActivity.class));
        finish();
    }
    private void addControls() {
        btnLogin = findViewById(R.id.btn_login);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        tvRegister = findViewById(R.id.tv_register);
        tcForgotPassword = findViewById(R.id.tv_forgot_password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("wait....");

        btnLoginFacebook = findViewById(R.id.btn_facebook);
        btnLoginGoogle = findViewById(R.id.btn_google);

        mAuth = FirebaseAuth.getInstance();
    }
}