package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.Retrieving_Data.Locations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class RegisterUserActivity extends AppCompatActivity{

    private ImageButton btn_back;
    private EditText name, phone, email, password, con_password;
    private Button btn_register;
    private TextView goto_register_maid;
    private FirebaseAuth auth ;
    private DatabaseReference ref;
    private String UID;
    private ProgressDialog progressDialog;

    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private HashMap<String,Object> Data = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        btn_back = findViewById(R.id.btn_back);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.con_password);

        btn_register = findViewById(R.id.btn_register);
        goto_register_maid = findViewById(R.id.goto_register_maid);


        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        ////////////////////////////////////////////////////////////

        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        con_password.addTextChangedListener(textWatcher);

        ////////////////////////////////////////////////////////////

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        goto_register_maid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUserActivity.this, RegisterMaidActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Confirm_Password = con_password.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Invalid email address.");
                    return;
                }
                else if (Password.length() < 8) {
                    password.setError("Password must be longer than 8 character.");
                    return;
                }
                else if (Confirm_Password.length() < 8) {
                    con_password.setError("Confirm Password must be longer than 8 character.");
                    return;
                }
                else if (!Password.equals(Confirm_Password)) {
                    con_password.setError("Confirm password must match the password.");
                    return;
                }
                else {
                    CreateAccount(Email,Password);
                }
            }
        });

    }

    private void CreateAccount(String email,String password) {
        progressDialog.setMessage("Created Account...");
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                SaveDataInDatabase(authResult.getUser().getUid());
                Toast.makeText(RegisterUserActivity.this, "Registered successfully. Please check your email for verification.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterUserActivity.this, "Account creation unsuccessful Please check the internet.", Toast.LENGTH_SHORT).show();
            }
        });
//        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                SaveDataInDatabase(auth.getCurrentUser().getUid());
//                                Toast.makeText(RegisterUserActivity.this, "Registered successfully. Please check your email for verification.", Toast.LENGTH_SHORT).show();
//                            }else{
//                                progressDialog.dismiss();
//                                Toast.makeText(RegisterUserActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }else{
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterUserActivity.this, "Account creation unsuccessful Please check the internet.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void SaveDataInDatabase(String Uid) {
//        Locations locations = new Locations(0.0,0.0);
        progressDialog.setMessage("Save Account Info...");
        String Name = name.getText().toString().trim();
        String Phone = phone.getText().toString().trim();
        String Email = email.getText().toString().trim();
        Data.put("name",Name);
        Data.put("phone",Phone);
        Data.put("email",Email);
        Data.put("profile_Url","");
        Data.put("position","user");
        Data.put("distance",50);
        Data.put("online","");
        Data.put("address","");
//        Data.put("location",locations);

        ref.child("Users").child(Uid).setValue(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                auth.signOut();
                Toast.makeText(RegisterUserActivity.this, "Successfully saved data.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        ref.child("Users").child(Uid).setValue(Data).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    auth.signOut();
//                    Toast.makeText(RegisterUserActivity.this, "Successfully saved data.", Toast.LENGTH_SHORT).show();
//                    onBackPressed();
//                    progressDialog.dismiss();
//                }else{
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterUserActivity.this, "Data recording failed. Please check the internet.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Name = name.getText().toString().trim();
            String Phone = phone.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();
            String Confirm_Password = con_password.getText().toString().trim();
            btn_register.setEnabled(
                    !Name.isEmpty() && !Phone.isEmpty()
                 && !Email.isEmpty() && (Phone.length() == 9 || Phone.length() == 10)
                 && !Password.isEmpty() && !Confirm_Password.isEmpty()
                 && Password.length() == 8 && Confirm_Password.length() == 8
            );
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}


