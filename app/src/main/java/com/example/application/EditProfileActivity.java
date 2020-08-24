package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.application.Retrieving_Data.ProfileUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageButton btn_back;
    private RelativeLayout edit_name,edit_password,edit_phone,edit_email,edit_gender;
    private TextView name,phone,email,gender;

    private BottomSheetDialog bottomSheetDialog;

    private String Uid;
    private FirebaseAuth auth;
    private DatabaseReference ref;

    private ProfileUserData profileUserData;

    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressDialog = new ProgressDialog(this);

        btn_back = findViewById(R.id.btn_back);

        edit_name = findViewById(R.id.edit_name);
        edit_password = findViewById(R.id.edit_password);
        edit_phone = findViewById(R.id.edit_phone);
        edit_email = findViewById(R.id.edit_email);
        edit_gender = findViewById(R.id.edit_gender);

        bottomSheetDialog = new BottomSheetDialog(EditProfileActivity.this);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);

        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser() == null){
           startActivity(new Intent(EditProfileActivity.this,LoginActivity.class));
           finish();
        }

        Query updateInfoUser = ref.child("Users").child(Uid);
        updateInfoUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    profileUserData = snapshot.getValue(ProfileUserData.class);
                    setInfoUser(profileUserData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit_name.setOnClickListener(new View.OnClickListener() {
            ImageButton cancel;
            TextView title;
            EditText edit_text;
            Button btn_clear_text,save;

            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet,(LinearLayout)findViewById(R.id.bottom_sheet));

                cancel = view.findViewById(R.id.cancel);
                title = view.findViewById(R.id.title);
                edit_text = view.findViewById(R.id.edit_text);
                btn_clear_text = view.findViewById(R.id.btn_clear_text);
                save = view.findViewById(R.id.save);

                title.setText("Full Name");
                edit_text.setText(profileUserData.getName());

                title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_18dp,0,0,0);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                btn_clear_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit_text.getText().clear();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        HashMap<String,Object> updataData = new HashMap<String, Object>();
                        if(!edit_text.getText().toString().isEmpty()){
                            progressDialog.setMessage("Saving...");
                            updataData.put("name",edit_text.getText().toString());
                            ref.child("Users").child(Uid).updateChildren(updataData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                bottomSheetDialog.dismiss();
                                            }
                                        }, 2000);
                                    }
                                }
                            });
                        }else{
                           edit_text.setError("Name required.");
                           progressDialog.dismiss();
                           return;
                        }
                    }
                });

                bottomSheetDialog.setCancelable(false);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
            }
        });

        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,UpdatePasswordActivity.class));
            }
        });

        edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: phone");
            }
        });

        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,UpdateEmailActivity.class));
                Log.d(TAG, "onClick: email");
            }
        });

        edit_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: gender");
            }
        });

    }

    private void setInfoUser(ProfileUserData profileUserData){
        name.setText(profileUserData.getName());
        email.setText(profileUserData.getEmail());
        phone.setText(profileUserData.getPhone());

    }

}