package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.wang.avi.AVLoadingIndicatorView;

public class ForgetPasswordActivity extends AppCompatActivity {

    private RelativeLayout container;

    private ImageButton btn_back;
    private EditText email;
    private Button btn_recover;
    private AVLoadingIndicatorView loading;

    private Dialog dialog;

    private FirebaseAuth auth;

    private static final String TAG = "ForgetPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

    ///////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *//////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    ///////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        container = findViewById(R.id.container);
        email = findViewById(R.id.email);
        btn_recover = findViewById(R.id.btn_recover);
        loading = findViewById(R.id.loading);

    /////////////////////////////////////*   เชื่อมต่อ Authentication  *//////////////////////////////////
        auth = FirebaseAuth.getInstance();

    //////////////////*   เชื่อมต่อกับ TextWatcher เพื่อตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *///////////////
        email.addTextChangedListener(textWatcher);

    /////////////////////*  Check is correct and Call Recover_Password Function  *//////////////////
        btn_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                    email.setError("Invalid email address.");
                    return;
                }
                else{
                    loading.smoothToShow();
                    Recover_Password(Email);
                }
            }
        });
    }

    ////////////////////*  Enter email and send reset password ไปยัง Email ดังกล่าว  */////////////////////
    private void Recover_Password(String Email){
        auth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ShowDialog();
                loading.smoothToHide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar snackbar = Snackbar.make(container, "This email was not found or invalid. Please check again.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                email.getText().clear();
                            }
                        });
                snackbar.show();
                loading.smoothToHide();
            }
        });
    }

    /////////////////////*  แสดง Dialog บอกสถานะว่าส่ง Reset password ไปยัง Email สำเร็จ  *//////////////////////
    private void ShowDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_true_resetpassword);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn_action = dialog.findViewById(R.id.btn_action);

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    ///////////////////////*   ตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  */////////////////////////////
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
}

/* Comments 1/10/2020 19:00 Check:True
    - กรณีที่ยังไม่ได้ยืนยันแล้วทำการเปลี่ยนแปลงรหัสผ่านก่อน หลังการที่เปลี่ยนแปลงรหัสผ่านเสร็จระบบจะทำการยืนยัน Email โดยอัตโนมัติ
*/

//    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

