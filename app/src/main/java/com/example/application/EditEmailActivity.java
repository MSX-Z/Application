package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditEmailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText current_email,password,new_email;
    private Button update_email;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private AuthCredential authCredential;

    private String Uid;
    private DatabaseReference ref;

    private static final String TAG = "EditEmailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Email");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_email = findViewById(R.id.current_email);
        password = findViewById(R.id.password);
        new_email = findViewById(R.id.new_email);

        update_email = findViewById(R.id.update_email);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(EditEmailActivity.this,LoginActivity.class));
            finish();
        }

        Uid = auth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(EditEmailActivity.this,LoginActivity.class));
            finish();
        }

        current_email.setText(auth.getCurrentUser().getEmail());

        update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = password.getText().toString().trim();
                String New_email = new_email.getText().toString().trim();

                if(Password.isEmpty()){
                    password.setError("Password is required.");
                    return;
                }else if(Password.length() < 8){
                    password.setError("Password must be longer than 8 character.");
                    return;
                }else if(New_email.isEmpty()){
                    new_email.setError("New email is required.");
                    return;
                }else if(!Patterns.EMAIL_ADDRESS.matcher(New_email).matches()) {
                    new_email.setError("Invalid email");
                    return;
                }else{
                    updateEmail(Password,New_email);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void updateEmail(final String Password, final String New_email) {
        progressDialog.setMessage("Update email wait...");
        progressDialog.show();
        authCredential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(),Password);
        auth.getCurrentUser().reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Password correct");
                auth.getCurrentUser().updateEmail(New_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savingEmail(New_email);
                        Toast.makeText(EditEmailActivity.this, "Email update successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditEmailActivity.this, "Email update unsuccessfully "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                password.getText().clear();
                new_email.getText().clear();
                Toast.makeText(EditEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savingEmail(String New_email) {
        progressDialog.setMessage("Saving email wait...");
        HashMap<String,Object> email = new HashMap<String, Object>();
        email.put("email",New_email);
        ref.child("Users").child(Uid).updateChildren(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(EditEmailActivity.this, "Saving email successfully.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}