package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.Adapter.ChatRecyclerAdapter;
import com.example.application.Retrieving_Data.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircularImageView profile;
    private TextView name, email;
    private ImageView status_online;
    private RecyclerView container_chat;
    private EditText message;
    private ImageButton btn_like, btn_send;

    private FirebaseAuth auth;

    private String KeyRoom;
    private String Uid;
    private String Uid_His;
    private String His_profile_Url;

    private DatabaseReference ref;
    private ValueEventListener seenListener;

    private List<Chat> chats;
    private ChatRecyclerAdapter chatRecyclerAdapter;

    private static final String TAG = "ChatActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);

        menu.findItem(R.id.menu).setTitle("Delete");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        if(itemID == R.id.menu) {
            test(KeyRoom);
//            Toast.makeText(this, "Hey Delete", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        status_online = findViewById(R.id.status_online);

        container_chat = findViewById(R.id.container_chat);

        message = findViewById(R.id.message);
        btn_like = findViewById(R.id.btn_like);
        btn_send = findViewById(R.id.btn_send);

        container_chat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
            finish();
        }

        Uid = auth.getCurrentUser().getUid();


        Intent intent = getIntent();
        if (intent.hasExtra("Uid_His")) {
            Uid_His = intent.getStringExtra("Uid_His");
            QueryHisInfo(Uid_His);
        }

        getKeyRoomChat();

        message.addTextChangedListener(textWatcher);

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    message.setHint("Type a message...");
                else
                    message.setHint("Aa");
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Message = message.getText().toString().trim();
                if(TextUtils.isEmpty(Message))
                    return;
                sendMessage(Message);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenListener);
    }

    private void getKeyRoomChat() {
        Query query = ref.child("Chat_lists").child(Uid).child(Uid_His);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    KeyRoom = snapshot.getValue(String.class);
                    readMessage();
                    seenMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage() {
        seenListener = ref.child("Chat").child(KeyRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if((chat.getReceiver().equals(Uid) && chat.getSender().equals(Uid_His))){
                        HashMap<String,Object> IsSeen = new HashMap<String, Object>();
                        IsSeen.put("isSeen",true);
                        ds.getRef().updateChildren(IsSeen).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Read Yes");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Read No "+e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        chats = new ArrayList<Chat>();
        ref.child("Chat").child(KeyRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Chat chat = ds.getValue(Chat.class);
                        if ((chat.getSender().equals(Uid) && chat.getReceiver().equals(Uid_His)) || (chat.getSender().equals(Uid_His) && chat.getReceiver().equals(Uid)))
                            chats.add(chat);
                    }
                }
                chatRecyclerAdapter = new ChatRecyclerAdapter(ChatActivity.this,chats,His_profile_Url,KeyRoom);
                chatRecyclerAdapter.notifyDataSetChanged();

                container_chat.setAdapter(chatRecyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String Message) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> dataMessage = new HashMap<String, Object>();
        dataMessage.put("sender",Uid);
        dataMessage.put("receiver",Uid_His);
        dataMessage.put("message",Message);
        dataMessage.put("isSeen",false);
        dataMessage.put("timestamp",timestamp);
        
        ref.child("Chat").child(KeyRoom).push().setValue(dataMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                checkAndUploadKeyRoomUserHis();
                Log.d(TAG, "onSuccess: message send");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

        message.getText().clear();
    }

    private void checkAndUploadKeyRoomUserHis() {
        Query query = ref.child("Chat_lists").child(Uid_His);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(Uid)){
                    HashMap<String,Object> keyRoom = new HashMap<String, Object>();
                    keyRoom.put(Uid,KeyRoom);
                    ref.child("Chat_lists").child(Uid_His).updateChildren(keyRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: up chat list success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void QueryHisInfo(String uid_his) {
        Query query = ref.child("Users").child(uid_his);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    His_profile_Url = snapshot.child("profile_Url").getValue(String.class);
                    String Name = snapshot.child("name").getValue(String.class);
                    String Email = snapshot.child("email").getValue(String.class);
                    boolean Online = snapshot.child("online").getValue(Boolean.class);
                    setHisInfo(His_profile_Url,Name,Email,Online);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setHisInfo(String His_profile_Url, String Name, String Email, boolean Online) {
        name.setText(Name);
        email.setText(Email);
        if(Online)
            status_online.setImageDrawable(getResources().getDrawable(R.drawable.ic_status_online));
        else
            status_online.setImageDrawable(getResources().getDrawable(R.drawable.ic_status_offonline));
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().isEmpty()) {
                btn_like.setVisibility(View.VISIBLE);
                btn_send.setVisibility(View.INVISIBLE);
            } else {
                btn_like.setVisibility(View.INVISIBLE);
                btn_send.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void test(String keyRoom){
//        Toast.makeText(this, ""+keyRoom, Toast.LENGTH_SHORT).show();
        String t1 = "/"+Uid+"/"+Uid_His;
        String t2 = "/"+Uid_His+"/"+Uid;

        HashMap<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(t1,null);
        childUpdates.put(t2,null);

        ref.child("Chat_lists").updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Y");    
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: N");
            }
        });

        ref.child("Chat").child(keyRoom).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: y");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: n");
            }
        });
//        Query query = ref.child("Chat_lists").child(Uid).orderByValue().equalTo(keyRoom);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    for(DataSnapshot ds:snapshot.getChildren())
//                        Log.d(TAG, "onDataChange: "+ds.toString());
//                }
//                else
//                    Log.d(TAG, "onDataChange: NO");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

}
/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(ChatActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
*/

/*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_bomtom_navbar);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
 */