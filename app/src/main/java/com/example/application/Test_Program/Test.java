package com.example.application.Test_Program;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.application.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Test extends AppCompatActivity{

//    private RecyclerView recyclerView;
//    private DatabaseReference ref1,ref2;
//    private RecyclerAdapter adapter;
//    private ArrayList<ProfileMaidData> item;
//    private ProfileMaidData position_me,position_maid;

    private TextView display;
    private DatabaseReference ref;
    private static final String TAG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ref = FirebaseDatabase.getInstance().getReference();

        Query query = ref.child("Test").child("41564");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: "+snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}

//        recyclerView = findViewById(R.id.my_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child("x5XlSrYslIW5dsIuuswi");
//        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    position_me = snapshot.getValue(ProfileMaidData.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        ref2 = FirebaseDatabase.getInstance().getReference().child("Users");
//        ref2.orderByChild("position").equalTo("maid").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    for (DataSnapshot ds:snapshot.getChildren()) {
//                        position_maid = ds.getValue(ProfileMaidData.class);
//                        double distance = Calculate_Distance(position_me,position_maid);
//                        if(distance <= 3)
//                            item.add(position_maid);
//                    }
//                    adapter = new RecyclerAdapter(getApplicationContext(),item);
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//    private double Calculate_Distance(ProfileMaidData x, ProfileMaidData y){
//        double lat1 = Math.toRadians(x.getLatitude());
//        double long1 = Math.toRadians(x.getLongitude());
//
//        double lat2 = Math.toRadians(y.getLatitude());
//        double long2 = Math.toRadians(y.getLongitude());
//
//        double dlat = lat2 - lat1;
//        double dlong = long2 - long1;
//
//        double ans = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlong/2), 2);
//        double KM = 2 * Math.asin(Math.sqrt(ans));
//
//        return KM * 6378.8;
//    }
//        ref2 = FirebaseDatabase.getInstance().getReference().child("Post");
//        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds:snapshot.getChildren()){
//                    ProfileMaidData data = ds.getValue(ProfileMaidData.class);
//                    if(!data.getEmployment())
//                        item.add(data);
//                }
//                adapter = new RecyclerAdapter(getApplicationContext(),item);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//Intent intent = getIntent();
//Uri[] imageURI = new Uri[]{intent.getParcelableExtra("Profile"),intent.getParcelableExtra("ID Card")};
//        for (int i = number; i < imageURI.length;) {
//            Uri uri = imageURI[i];
//            SaveImage(uri,(i+1));
//        }
//        Log.d("AAAAAAAAAAAAAAAAA", ""+imageURI.length);
//        ref = FirebaseDatabase.getInstance().getReference("users");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                DatabaseReference x = ref;
//                if(snapshot.exists()){
//                    for(DataSnapshot ds : snapshot.getChildren()){
//                        final String key = ds.getKey();
//                        x.child(key).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if(snapshot.exists()){
//                                    if(Integer.parseInt(snapshot.child("value").getValue().toString()) == 30){
//                                        Log.d("AAAAAAAA", ""+key);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//    private void SaveImage(Uri uri, int i) {
//        String pathImage = "profile_image/qewrtrytyjku23456u7iujsd32";
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(pathImage).child("Profile_"+""+i+".jpg");
//        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                number += 1;
//                Log.d("AAAAAAAA", "Successfully");
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("AAAAAAAA", ""+e.getMessage());
//            }
//        });
//    }

//        Intent intent = getIntent();
//        HashMap<String, Object> hashMap = (HashMap<String, Object>)intent.getSerializableExtra("ProfileMaidData");
//        ProfileMaidData data = new ProfileMaidData(hashMap);
//        Log.d("AAAAAAAAAAA", data.getImageURI()+"\n"+
//                                        data.getName()+"\n"+
//                                        data.getAddress()+"\n"+
//                                        data.getCounty()+"\n"+
//                                        data.getState()+"\n"+
//                                        data.getCity()+"\n"+
//                                        data.getEmail()+"\n"+
//                                        data.getPassword()+"\n"+
//                                        data.getConfirmPassword());