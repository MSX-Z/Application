package com.example.application;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private RelativeLayout container_phone_verify;

    private ImageButton btn_back;
    private EditText code;
    private Button btn_verify;

    private AVLoadingIndicatorView loading;
    private Snackbar snackbar;

    private String verificationCodeBySystem;

    private HashMap<String,Object> hashMap;

    private String Position;
    private String Uid = null;

    private FirebaseAuth auth ;
    private DatabaseReference ref;
    private StorageReference storageReference;

    private static final String TAG = "PhoneVerifyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

    /////////////////////////*      getIntent เพื่อรับ intent       *////////////////////////////////////
        Intent getIntent = getIntent();

    ///////////*    check intent box ว่ามามี intent box "CGA" ไหม แล้ว แยกฝั่งด้วย Position *////////////////
        if(getIntent.hasExtra("CGA")) {
            hashMap = (HashMap<String, Object>) (getIntent.getSerializableExtra("CGA"));
            Position = hashMap.get("position").toString();
            Log.d(TAG, "onCreate: position "+Position);
        } else {
            Intent i = new Intent(PhoneVerifyActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    //////////////////////////////////*  ดึงหมายเลขโทรศัทพ์จาก hashMap */////////////////////////////////////
        String phone = hashMap.get("phone").toString().trim();

        ////////////////////*   เชื่อมต่อ Authentication Database and Storage  *//////////////////////////
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    ///////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *//////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    ////////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        container_phone_verify = findViewById(R.id.container_phone_verify);
        code = findViewById(R.id.code);
        btn_verify = findViewById(R.id.btn_verify);
        loading = findViewById(R.id.loading);

    /////////////////*   เชื่อมต่อกับ TextWatcher เพื่อตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *////////////////
        code.addTextChangedListener(textWatcher);

    ////////////////////////////*   Call  sendVerificationCodeToUser function *//////////////////////
        sendVerificationCodeToUser(phone);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CODE = code.getText().toString().trim();
                if(CODE.length() < 6){
                    code.setError("Verification code must be longer than 8 character.");
                    return;
                }
                verifyCode(CODE);
            }
        });
    }

    ////////////////////////*   sendVerificationCodeToUser function   */////////////////////////////
    private void sendVerificationCodeToUser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+66"+phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    /////////////////////////*   Verification ว่าสำเร็จหรือไม่  *////////////////////////////////////////////
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneVerifyActivity.this, "onVerificationFailed "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    //////////////////////////*     phoneAuthCredential get จาก code ที่ส่งมาทาง sms   *///////////////////
    private void verifyCode(String codeByUser) {
        loading.smoothToShow();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
        createAccount(phoneAuthCredential);
    }

    /////*  Create account user and send email verify จากนั้น Call SaveDataInDatabase Function เมื่อสร้าง Account และ ส่ง Email verify สำเร็จ */////
    private void createAccount(PhoneAuthCredential phoneAuthCredential) {
        if(phoneAuthCredential != null){
            snackbar = Snackbar.make(container_phone_verify,"Create Account...",Snackbar.LENGTH_SHORT);
            snackbar.show();
            Log.d(TAG, "createAccount: "+hashMap.toString());
            String email = hashMap.get("email").toString().trim();
            String password = hashMap.get("password").toString().trim();
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Uid = auth.getCurrentUser().getUid();
                            checkPositions();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.smoothToHide();
                            Toast.makeText(PhoneVerifyActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.smoothToHide();
                    Toast.makeText(PhoneVerifyActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Log.d(TAG, "createAccount: Failed haven't phoneAuthCredential");
        }
    }

    ///////////////////////*   ตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *//////////////////////////////
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String CODE = code.getText().toString().trim();
            btn_verify.setEnabled(!CODE.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /////////////////////////////////* ตรวจสอบว่าเป็นฝั่ง user หรือ maid ก่อน *//////////////////////////////////
    private void checkPositions(){
        if(Position.equals("user"))
            saveDataInDatabase(Uid);
        else if(Position.equals("maid")){
            List<byte[]> imageList =  new ArrayList<byte[]>();
            imageList.add((byte[]) hashMap.get("IdCard_Uri"));
            imageList.add((byte[]) hashMap.get("Profile_Uri"));
            uploadImage(imageList);
        }
    }

    /////////////////////////* upload id_card and profile ของ maid */////////////////////////////////
    private void uploadImage(final List<byte[]> imageList) {
        for(int i = 0;i < imageList.size();i++){
            final String path,hashKey;
            final int finalI = i;
            path = (i == 0) ? "id_card" : "profile";
            hashKey = (i == 0) ? "IdCard_Uri" : "Profile_Uri";
            byte[] getImage = imageList.get(i);
            StorageReference imagePath = storageReference.child(Position).child(Uid).child(path).child(path+".jpg");
            UploadTask uploadTask = imagePath.putBytes(getImage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();
                    if(uriTask.isSuccessful())
                        hashMap.put(hashKey,downloadUri.toString());
                    if(finalI == imageList.size()-1)
                        saveDataInDatabase(Uid);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.smoothToHide();
                    Log.d(TAG, "onFailure: "+e.getMessage());
                }
            });
        }
    }

    ///////////////////////////*  บันทึกข้อมูลลงไปใน database  *////////////////////////////////////////////
    private void saveDataInDatabase(String uid){
        if(Uid == null)
            return;

        hashMap.remove("password");
        hashMap.remove("confirm_password");

        ref.child("Users").child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                snackbar = Snackbar.make(container_phone_verify,"Registered successfully. Please check your email for verification.",Snackbar.LENGTH_SHORT);
                snackbar.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.smoothToHide();
                        Intent i = new Intent(PhoneVerifyActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                },2000);

                Log.d(TAG, "onSuccess: Saving Successfully. You is "+Position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.smoothToHide();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }
}

/* Comments 1/10/2020 19:00 Check:True
    - checkPositions:
        - ถ้าเป็น user ในบันทึกข้อมูลเลยทันที
        - ถ้าเป็น maid ในบันทึกรูปภาพ id_card และ profile ลงไปก่อนแล้วถึงค่อยบันทึกข้อมูลที่หลัง
    - uploadImage:
        - ขั้นแรก ดึงรูปภาพที่ส่งมาจาก hashMap ก่อน (Compressor image นั้นและ)
        - นำรูปไป up ขึ้น Storage firebase แล้ว get URL จาก Storage firebase มา
        - แล้ว put กลับไปที่ key เดิมของ hashMap ที่ได้ทำการ get มา
        (สรุป key hashKey ดังกล่าวจะถูกเปลี่ยนแปลงไปเป็น URL)
*/