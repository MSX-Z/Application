package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import java.util.HashMap;

public class ChooseGenderActivity extends AppCompatActivity {

    private ImageButton btn_back;

    private CircularImageView male,female;
    private ImageView tick_male,tick_female;

    private Button btn_next;
    private HashMap<String,Object> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_gender);

    /////////////////////////*      getIntent เพื่อรับ intent       *////////////////////////////////////
        Intent getIntent = getIntent();

    //*    check intent box ว่ามามี intent box "RUA" หรือ "PFA" ไหม แล้ว hashMap ที่ได้จะเป็นข้อมูลของฝั่ง user หรือ maid *//
        if(getIntent.hasExtra("RUA"))
            hashMap = (HashMap<String, Object>) getIntent.getSerializableExtra("RUA");

        else if(getIntent.hasExtra("PFA"))
            hashMap = (HashMap<String, Object>) getIntent.getSerializableExtra("PFA");
        else {
            Intent i = new Intent(ChooseGenderActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    ///////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *//////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    ///////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        tick_male = findViewById(R.id.tick_male);
        tick_female = findViewById(R.id.tick_female);
        btn_next = findViewById(R.id.btn_next);

    //////////////*    put(key = "gender": male) ลงไปใน hashMap ของ user หรือ maid  *////////////////////
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTick(tick_male,tick_female);
                hashMap.put("gender","male");
            }
        });

    //////////////*    put(key = "gender":female) ลงไปใน hashMap ของ user หรือ maid  *////////////////////
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTick(tick_female,tick_male);
                hashMap.put("gender","female");
            }
        });

    /////////*      กดถัดไปแล้ว passing data ไปยัง PhoneVerifyActivity ด้วย intent box "CGA"  *//////////////
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseGenderActivity.this,PhoneVerifyActivity.class);
                intent.putExtra("CGA",hashMap);
                startActivity(intent);
            }
        });
    }

    ////////////////////*       toggle เครื่องหมายถูกว่าเลือก Gender อะไรอยู่     *///////////////////////////////
    private void toggleTick(ImageView tick_1, ImageView tick_2) {
        tick_1.setVisibility(View.VISIBLE);
        tick_2.setVisibility(View.INVISIBLE);
        btn_next.setEnabled(true);
    }
}

/* Comments 1/10/2020 19:00 Check:True
    - กรณีไม่มี intent ที่ต้องการจะกลับไปยัง LoginActivity
    - hashMap จะเป็นข้อมูลของ user "หรือ" maid ขึ้นอยู่กับ intent box ที่รับเข้ามา
    - Intent box
        - "RUA" มาจาก RegisterUserActivity ฝั่งของ User
        - "PFA" มาจาก ProfileActivity ผั่งของ maid

    - สุดท้ายข้อมูลทั้งหมดทั้งฝั่ง user หรือ maid จะถูกส่งไปยัง PhoneVerifyActivity ด้วย Intent box "CGA"
*/