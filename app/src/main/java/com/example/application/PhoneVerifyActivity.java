package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Test_Program.Test;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private TextView code,text_number;
    private EditText phone;
    private Button btn_send_otp;
    private AVLoadingIndicatorView avi;

    private String verificationCodeBySystem;

    private HashMap<String,Object> hashMap;

    private FirebaseAuth fAuth;
    private ProgressDialog progressDialog;

    private String SMS_Code = "XXXXXX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        btn_back = findViewById(R.id.btn_back);
        code = findViewById(R.id.code);
        text_number = findViewById(R.id.text_number);
        phone = findViewById(R.id.phone);
        btn_send_otp = findViewById(R.id.btn_send_otp);
        avi = findViewById(R.id.loading);

        fAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        ////////////////////////////////////////////////////////////

        hashMap = (HashMap<String, Object>) getIntent().getSerializableExtra("ProfileMaidData");

        ////////////////////////////////////////////////////////////

        phone.addTextChangedListener(textWatcher);

        ////////////////////////////////////////////////////////////


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Phone = phone.getText().toString().trim();
                if(Phone.length() != 10){
                    phone.setError("Invalid phone number");
                    return;
                }
                else{
                    avi.setVisibility(View.VISIBLE);
                    sendVerificationCodeToUser(Phone);
                }
            }
        });
    }

    private void sendVerificationCodeToUser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+66"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String Code = phoneAuthCredential.getSmsCode();
            if(Code != null){
                SMS_Code = Code;
                verifyCode(Code);
            }
            else{
                Toast.makeText(PhoneVerifyActivity.this, "Please enter your mobile phone.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneVerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,Code);
        code.setText(SMS_Code);
        code.setVisibility(View.VISIBLE);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {//PhoneAuthCredential credential
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        if(SMS_Code.equals("XXXXXX") || credential == null){
            Toast.makeText(PhoneVerifyActivity.this, "Please enter your mobile phone.", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = hashMap.get("Email").toString();
        String password = hashMap.get("Password").toString();

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SaveToDatabase();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(PhoneVerifyActivity.this, "Crating Not Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        fAuth.signInWithCredential(credential).addOnCompleteListener(PhoneVerifyActivity.this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(PhoneVerifyActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(PhoneVerifyActivity.this,Test.class));
//                }
//                else {
//                    Toast.makeText(PhoneVerifyActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void SaveToDatabase() {
        progressDialog.setMessage("Saving Account Info...");

        Uri profile = (Uri)hashMap.get("ImageURI");
        String pathImage = "profile_image/"+""+fAuth.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(pathImage).child("Profile.jpg");
        storageReference.putFile(profile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri Download_Profile = uriTask.getResult();

                if(uriTask.isSuccessful()){
                    hashMap.put("ImageURI",""+Download_Profile);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(fAuth.getCurrentUser().getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(PhoneVerifyActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PhoneVerifyActivity.this, Test.class));
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(PhoneVerifyActivity.this, "Not Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PhoneVerifyActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Phone = phone.getText().toString().trim();
            if(!Phone.isEmpty()){
                text_number.setText("+66 "+Phone);
            }
            else{
                text_number.setText("+66 Your Phone Number");
            }
            btn_send_otp.setEnabled(!Phone.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
