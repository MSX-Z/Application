package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ChooseSignUpActivity extends AppCompatActivity {

    private CircularImageView user,maid;
    private ImageView btn_back,tick_user,tick_maid;
    private TextView desc;
    private Button btn_next;

    private String Goto = "";

    private Animation animFadeIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sign_up);

    ////////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    ///////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        user = findViewById(R.id.user);
        maid = findViewById(R.id.maid);
        tick_user = findViewById(R.id.tick_user);
        tick_maid = findViewById(R.id.tick_maid);
        desc = findViewById(R.id.desc);
        btn_next = findViewById(R.id.btn_next);

    ///////////////////////////*    Animation fade in   *///////////////////////////////////////////
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);


    //////////////////////////*     กรณีที่ Choose Position User    *///////////////////////////////////
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goto = "user";
                toggleTick(tick_user,tick_maid);
                desc.setText("Users around the world can find employment, contact us for further information");
                fadeOutDesc(desc);
            }
        });

    //////////////////////////*     กรณีที่ Choose Position User    *///////////////////////////////////
        maid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Goto = "maid";
                toggleTick(tick_maid,tick_user);
                desc.setText("Maids can create jobs and provide additional information to general users.");
                fadeOutDesc(desc);
            }
        });

    /////////////////////////*      กดถัดไปได้ก็ต่อเมื่อหลังจากเลือก Position แล้ว      *//////////////////////////////
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Goto.equals("") && Goto.equals("user"))
                    startActivity(new Intent(ChooseSignUpActivity.this,RegisterUserActivity.class));
                else if(!Goto.equals("") && Goto.equals("maid"))
                    startActivity(new Intent(ChooseSignUpActivity.this,RegisterMaidActivity.class));
            }
        });
    }

    /////////////////////*      Animation Function  *///////////////////////////////////////////////
    private void fadeOutDesc(TextView desc) {
        desc.setVisibility(View.VISIBLE);
        desc.startAnimation(animFadeIn);
    }

    ////////////////////*       toggle เครื่องหมายถูกว่าเลือก Position อะไรอยู่     *///////////////////////////////
    private void toggleTick(ImageView tick_1, ImageView tick_2) {
        tick_1.setVisibility(View.VISIBLE);
        tick_2.setVisibility(View.INVISIBLE);
        btn_next.setEnabled(true);
    }
}

/* Comments 1/10/2020 19:00 Check:True
        - ถ้าเลือก User จะไปที่ Activity => RegisterUserActivity
        - ถ้าเลือก Maid จะไปที่ Activity => RegisterMaidActivity
*/