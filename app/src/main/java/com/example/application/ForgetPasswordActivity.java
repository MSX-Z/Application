package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgetPasswordActivity";

    private ImageButton btn_back;
    private EditText email;
    private Button btn_recover;
    private ProgressDialog progressDialog;
    private Dialog dialog;

    private FirebaseAuth auth;

    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btn_back = findViewById(R.id.btn_back);
        email = findViewById(R.id.email);
        btn_recover = findViewById(R.id.btn_recover);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ////////////////////////////////////////////////////////////

        email.addTextChangedListener(textWatcher);

        ////////////////////////////////////////////////////////////

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();

                if(!Email.matches(EmailPattern)){
                    email.setError("Invalid email address.");
                    return;
                }
                else{
                    Recover_Password(Email);
                }
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Email = email.getText().toString().trim();
            btn_recover.setEnabled(!Email.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void Recover_Password(String Email){
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        auth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AAAAAAAAAAAAAA", "onSuccess: YES");
                ShowDialog(ForgetPasswordActivity.this, R.layout.dialog_true_resetpassword);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("AAAAAAAAAAAAAA", "onFailure: NO");
                ShowDialog(ForgetPasswordActivity.this, R.layout.dialog_false_resetpassword);
                progressDialog.dismiss();
            }
        });
//        }else{
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    progressDialog.dismiss();
//                    ShowDialog(ForgetPasswordActivity.this,R.layout.dialog_false_verify);
//                }
//            },2000);
//
//        }
    }

    private void ShowDialog(Context context, int layout) {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn_action = dialog.findViewById(R.id.btn_action);

        if(layout == R.layout.dialog_true_resetpassword) {
            btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                    finish();
                    dialog.dismiss();
                }
            });
        }else{
            btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }
}
