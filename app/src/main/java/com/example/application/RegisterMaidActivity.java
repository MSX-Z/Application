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

public class RegisterMaidActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private EditText name,phone,email,number_id,password,confirm_password;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maid);

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
        number_id = findViewById(R.id.number_id);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.con_password);
        btn_next = findViewById(R.id.btn_next);

    //////////////////*   เชื่อมต่อกับ TextWatcher เพื่อตรวจสอบการเปลี่ยนแปลง Object View ของ EditText  *///////////////
        name.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        number_id.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirm_password.addTextChangedListener(textWatcher);

    /////////////////*  Check is correct and Call passingDataToActivity Function  */////////////////
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Phone = phone.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Number_id = number_id.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Confirm_password = confirm_password.getText().toString().trim();

                if(Phone.length() < 9){
                    phone.setError("Phone must be longer than 9-10 character.");
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    email.setError("Invalid email address.");
                    return;
                }
                else if(Number_id.length() < 13){
                    number_id.setError("ID Card must be longer than 13 character.");
                }
                else if(!checkIDCard(Number_id)){
                    number_id.setError("ID Card incorrect.");
                }
                else if (Password.length() < 8) {
                    password.setError("Password must be longer than 8 character.");
                    return;
                }
                else if (Confirm_password.length() < 8) {
                    confirm_password.setError("Confirm Password must be longer than 8 character.");
                    return;
                }
                else if (!Password.equals(Confirm_password)) {
                    confirm_password.setError("Confirm password must match the password.");
                    return;
                }
                else {
                    passingDataToActivity(Name,Phone,Email,Password,Confirm_password);
                }
            }
        });
    }

    ////////*  Function ในการ passing data ระหว่าง RegisterUserActivity กับ IDCardActivity  *////////
    private void passingDataToActivity(String name, String phone, String email, String password, String confirm_password) {
        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put("name",name);
        hashMap.put("phone",phone);
        hashMap.put("email",email);
        hashMap.put("password",password);
        hashMap.put("confirm_password",confirm_password);
        hashMap.put("address","");
        hashMap.put("online","");
        hashMap.put("position","maid");
        hashMap.put("typeTo","noOne");
        hashMap.put("verify",0);
        hashMap.put("rating",0);


        Intent intent = new Intent(RegisterMaidActivity.this,IDCardActivity.class);
        intent.putExtra("RMA",hashMap);
        startActivity(intent);
    }

    ////////////////////////////*   Check the validity of the ID card code. *///////////////////////
    private boolean checkIDCard(String id){
        int sum = 0;
        for(int i = 0;i < id.length()-1;i++) {
            int num = Character.digit(id.charAt(i),10);
            sum += (num * (13 - i));
        }
        String result = String.valueOf((11 - (sum%11))%10);
        return result.equals(String.valueOf(id.charAt(12)));
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
            String Number_id = number_id.getText().toString().trim();
            String Password = password.getText().toString().trim();
            String Confirm_password = confirm_password.getText().toString().trim();

            btn_next.setEnabled(
                    !Name.isEmpty() && !Phone.isEmpty()
                 && !Email.isEmpty() && (Phone.length() == 9 || Phone.length() == 10)
                 && !Number_id.isEmpty() && (Number_id.length() == 13)
                 && !Password.isEmpty() && !Confirm_password.isEmpty()
                 && Password.length() == 8 && Confirm_password.length() == 8
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
        - address ( กรณีที่ยังไม่มี Address ต้องระบุก่อนโพสต์งาน )
        - online
        - position
        - typeTo
        - verify
        - rating
        ( ส่งผ่าน Intent box = "RMA" ไปยัง Activity IDCardActivity)
*/