package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class UpdatePasswordActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private EditText current_password,new_password,con_new_password;
    private Button update_password;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private AuthCredential authCredential;

    private static final String TAG = "UpdatePasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        btn_back = findViewById(R.id.btn_back);

        current_password = findViewById(R.id.currant_password);
        new_password = findViewById(R.id.new_password);
        con_new_password = findViewById(R.id.con_new_password);

        update_password = findViewById(R.id.update_password);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(UpdatePasswordActivity.this,LoginActivity.class));
            finish();
        }

        progressDialog = new ProgressDialog(this);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Update wait...");

                String Current_Password = current_password.getText().toString().trim();
                String New_Password = new_password.getText().toString().trim();
                String Con_New_Password = con_new_password.getText().toString().trim();

                if(Current_Password.isEmpty()){
                    current_password.setError("Current password is required.");
                    return;
                }else if(New_Password.isEmpty()){
                    new_password.setError("New password is required.");
                    return;
                }else if(New_Password.length() < 8){
                    new_password.setError("New password must be longer than 8 character.");
                    return;
                }else if(Con_New_Password.isEmpty()){
                    con_new_password.setError("Confirm password is required.");
                    return;
                }else if(Con_New_Password.length() < 8){
                    con_new_password.setError("Confirm new password must be longer than 8 character.");
                    return;
                }else if (!New_Password.equals(Con_New_Password)) {
                    con_new_password.setError("Confirm password must match the password.");
                    return;
                }else{
                    update_password(Current_Password,Con_New_Password);
                }
            }
        });
    }

    private void update_password(final String Current_password, final String New_password) {
        progressDialog.show();
        authCredential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(),Current_password);
        auth.getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Password Success");
                auth.getCurrentUser().updatePassword(New_password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Update Password Success.");
                        Toast.makeText(UpdatePasswordActivity.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: Password Unsuccess"+e.getMessage());
                Toast.makeText(UpdatePasswordActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                current_password.getText().clear();
                new_password.getText().clear();
                con_new_password.getText().clear();
            }
        });
    }
}