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
import android.util.Patterns;
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
    private TextView btn_forget_password,btn_sign_up;
    private Button login;
    private AVLoadingIndicatorView loading;

    private FirebaseAuth auth;

    private boolean doubleBackToExitPressedOnce = false;

    /////////////////////////////*  ปุ่ม Back ของโทรศัพท์ จะต้องกด 2 ครั้งเพื่อออก  *//////////////////////////////////
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");

    ////////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_forget_password = findViewById(R.id.btn_forget_password);
        login = findViewById(R.id.login);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        loading = findViewById(R.id.loading);

    ///////////////////////////////////*   เชื่อมต่อ Authentication */////////////////////////////////////
        auth = FirebaseAuth.getInstance();

    /////////////////*   เชื่อมต่อกับ TextWatcher เพื่อตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *////////////////
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

    ////////////////////////*   Auto Login เมื่อมี User และ ได้ทำการ Verify Email แล้ว   */////////////////////
        if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

    /////////////////////////*  Check is correct and Call Login Function  */////////////////////////
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    email.setError("Invalid email address.");
                    return;
                }
                else if(Password.length() < 8){
                    password.setError("Password must be longer than 8 character.");
                    return;
                }
                else{
                    loading.smoothToShow();
                    Login(Email,Password);
                }
            }
        });

    /////////////////////////////*  Action ไปหน้า ForgetPasswordActivity  *////////////////////////////
        btn_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });

    /////////////////////////////*  Action ไปหน้า ForgetPasswordActivity  *////////////////////////////
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ChooseSignUpActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }



    ///////////////////////////*  Login Function and isEmailVerified  *//////////////////////////////
    private void Login(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(authResult.getUser().isEmailVerified()){
                    Log.d(TAG, "onSuccess: "+authResult.getUser().isEmailVerified());
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else
                    Toast.makeText(LoginActivity.this, "Please confirm your email.", Toast.LENGTH_SHORT).show();
                loading.smoothToHide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                loading.smoothToHide();
            }
        });
    }

    ///////////////////////*   ตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *//////////////////////////////
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


/* Comments 1/10/2020 19:00 Check:True
    - Auto Login ก็ต่อเมื่อ มีผู้ใช้งานคนปัจจุบันของมือถือเครื่องนั้นๆ ค้างอยู่ในระบบ และ ผู้ใช้งานคนดังกล่างจะต้อง Verify Email แล้ว
*/

//    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

//    private ConnectivityManager connectivityManager;
//    private NetworkInfo networkInfo;

//    private boolean isConnection(){
//        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        networkInfo = connectivityManager.getActiveNetworkInfo();
//        return (networkInfo != null) && networkInfo.isConnected();
//    }

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