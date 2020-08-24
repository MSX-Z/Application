package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircularImageView profile;
    private TextView name,email;
    private RecyclerView container_chat;
    private EditText message;
    private ImageButton btn_like,btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        profile = findViewById(R.id.profile);
//        name = findViewById(R.id.name);
//        email = findViewById(R.id.email);
//
//        container_chat = findViewById(R.id.container_chat);
//
//        message = findViewById(R.id.message);
//        btn_like = findViewById(R.id.btn_like);
//        btn_send = findViewById(R.id.btn_send);



    }
}