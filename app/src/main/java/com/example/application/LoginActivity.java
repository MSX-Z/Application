package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText email,password;
    private TextView forget_password,goto_register;
    private Button login;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forget_password = findViewById(R.id.forget_password);
        login = findViewById(R.id.login);
        goto_register = findViewById(R.id.goto_register);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        ////////////////////////////////////////////////////////////

        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        ////////////////////////////////////////////////////////////

        if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()){
            Log.d(TAG, "onCreate: if "+auth.getCurrentUser().getUid()+" "+auth.getCurrentUser().getEmail()+" "+auth.getCurrentUser().isEmailVerified());
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }else{
//            Log.d(TAG, "onCreate: else "+auth.getCurrentUser().getUid()+" "+auth.getCurrentUser().getEmail()+" "+auth.getCurrentUser().isEmailVerified());
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if(!Email.matches(EmailPattern)){
                    email.setError("Invalid email address.");
                    return;
                }
                else if(Password.length() < 8){
                    password.setError("Password must be longer than 8 character.");
                    return;
                }
                else{
                    Login(Email,Password);
                }
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });

        goto_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterUserActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart Have User: "+auth.getCurrentUser());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(LoginActivity.this, "Please press back again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void Login(String email,String password){
        progressDialog.setMessage("Please wait login ...");
        progressDialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(authResult.getUser().isEmailVerified()){
                    Log.d(TAG, "onSuccess: "+authResult.getUser().isEmailVerified());
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(LoginActivity.this, "Please confirm your email.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
//        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    if(auth.getCurrentUser().isEmailVerified()){
//                        Log.d(TAG, "onComplete: Is Verify "+auth.getCurrentUser().isEmailVerified());
//
//                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
//                        finish();
//                    }else{
//                        Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    if(!isConnection())
//                        Toast.makeText(LoginActivity.this, "No internet connection. Please check your connection.", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(LoginActivity.this, "This account could not be found or the password is incorrect. Please check again.", Toast.LENGTH_SHORT).show();
//                }
//                progressDialog.dismiss();
//            }
//        });
    }

    private boolean isConnection(){
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }
    
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();
            login.setEnabled(!Email.isEmpty() && !Password.isEmpty() && Password.length() == 8);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
