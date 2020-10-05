package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.HashMap;


public class RegisterUserActivity extends AppCompatActivity{

    private ImageButton btn_back;
    private EditText name, phone, email, password, con_password;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

    ///////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *//////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    ///////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.con_password);
        btn_next = findViewById(R.id.btn_next);


    //////////////////*   เชื่อมต่อกับ TextWatcher เพื่อตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *///////////////
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        con_password.addTextChangedListener(textWatcher);

    ////////////////*  Check is correct and Call passingDataToActivity Function  *//////////////////
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Phone = phone.getText().toString().trim();
                String Confirm_Password = con_password.getText().toString().trim();

                if(Phone.length() < 9){
                    phone.setError("Phone must be longer than 9-10 character.");
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
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
                    passingDataToActivity(Name,Phone,Email,Password,Confirm_Password);
                }
            }
        });

    }

    ////////*  Function ในการ passing data ระหว่าง RegisterUserActivity กับ ChooseGenderActivity  *////////
    private void passingDataToActivity(String name, String phone, String email, String password, String confirm_password) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("name",name);
        hashMap.put("phone",phone);
        hashMap.put("email",email);
        hashMap.put("password",password);
        hashMap.put("confirm_password",confirm_password);
        hashMap.put("address","");
        hashMap.put("online","");
        hashMap.put("position","user");
        hashMap.put("distance",50);
        hashMap.put("profile_Url","");
        hashMap.put("typeTo","noOne");

        Intent intent = new Intent(RegisterUserActivity.this,ChooseGenderActivity.class);
        intent.putExtra("RUA",hashMap);
        startActivity(intent);
    }


    ///////////////////////*   ตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  */////////////////////////////
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
            btn_next.setEnabled(
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

/* Comments 1/10/2020 19:00 Check:True
    - ข้อมูล:
        - name
        - phone
        - email
        - password
        - confirm_password
        - address ( กรณีที่ยังไม่มี Address ต้องระบุก่อนจ้างงาน )
        - online
        - position
        - distance
        - profile_Url
        - typeTo
        ( ส่งผ่าน Intent box = "RUA" ไปยัง Activity ChooseGenderActivity)
*/

//        private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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



/*
        /////*  Create account user and send email verify จากนั้น Call SaveDataInDatabase Function เมื่อสร้าง Account และ ส่ง Email verify สำเร็จ */////
//
//    private void CreateAccount(String email,String password) {
//        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        SaveDataInDatabase(auth.getCurrentUser().getUid());
//                        Toast.makeText(RegisterUserActivity.this, "Registered successfully. Please check your email for verification.", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        loading.smoothToHide();
//                        Toast.makeText(RegisterUserActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                loading.smoothToHide();
//                Toast.makeText(RegisterUserActivity.this, "Account creation unsuccessful Please check the internet."+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /////*  Save info user and back to Login Activity จากนั้น User ต้องตรวจสอบ Email ก่อนการดำเนินการ Login */////

//    private void SaveDataInDatabase(String Uid) {
//        String Name = name.getText().toString().trim();
//        String Phone = phone.getText().toString().trim();
//        String Email = email.getText().toString().trim();
//
//        ProfileUserData profileUserData = new ProfileUserData("",50,Email,Name,true,Phone,"user","");

//        Data.put("address","");/
//        Data.put("distance",50);/
//        Data.put("email",Email);/
//        Data.put("name",Name);/
//        Data.put("online","");/
//        Data.put("phone",Phone);/
//        Data.put("position","user");/
//        Data.put("profile_Url","");
//        Data.put("typeTo","");
//
//
//        ref.child("Users").child(Uid).setValue(profileUserData).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                auth.signOut();
//                Toast.makeText(RegisterUserActivity.this, "Successfully saved data.", Toast.LENGTH_SHORT).show();
//                onBackPressed();
//                loading.smoothToHide();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                loading.smoothToHide();
//                Toast.makeText(RegisterUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
