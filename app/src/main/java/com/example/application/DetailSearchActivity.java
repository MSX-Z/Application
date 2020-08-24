package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailSearchActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_search);
//
//        Intent intent = getIntent();
//
//        distance = findViewById(R.id.distance);
//        btn_back = findViewById(R.id.btn_back);
//
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        if(intent.hasExtra("distance")) {
//            int d = intent.getIntExtra("distance",0);
//            distance.setText(d+" Km.");
//        }
//        textView.setText(distance);
    }
}