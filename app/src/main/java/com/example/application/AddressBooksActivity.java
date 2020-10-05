package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Adapter.AddressRecyclerAdapter;
import com.example.application.Retrieving_Data.Address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressBooksActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private AddressRecyclerAdapter addressRecyclerAdapter;

    private LinearLayout linear_layout;
    private ImageView image_no_item;
    private TextView title_no_item, desc_no_item;

    private List<Address> address;

    private FirebaseAuth auth;
    private String Uid;
    private DatabaseReference ref;

    private static final String TAG = "AddressBooksActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_books);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Address Books");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.container_address);

        linear_layout = findViewById(R.id.linear_layout);
        image_no_item = findViewById(R.id.image_no_item);
        title_no_item = findViewById(R.id.title_no_item);
        desc_no_item = findViewById(R.id.desc_no_item);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser().getUid() == null){
            startActivity(new Intent(AddressBooksActivity.this,LoginActivity.class));
            finish();
        }

        Uid = auth.getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        address = new ArrayList<Address>();
        addressRecyclerAdapter = new AddressRecyclerAdapter(this,address);
        recyclerView.setAdapter(addressRecyclerAdapter);

        queryYourAddress();
//        address.add(new Address("70/46 m.4 Wat Tom Ayutthaya Ayutthaya 13000 70/46 m.4 Wat Tom Ayutthaya Ayutthaya 13000","MSXZ","0812345678",true));
    }


    private void queryYourAddress() {
        Query query = ref.child("Address_Books").child(Uid).orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                address.clear();
                if(snapshot.exists()){
                    linear_layout.setVisibility(View.INVISIBLE);
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Address ad = ds.getValue(Address.class);
                        if(ad.isUsability())
                            address.add(0,ad);
                        else
                            address.add(ad);
                    }
                    addressRecyclerAdapter.notifyDataSetChanged();
                    updateChildAddress(address.get(0));
                }else{
                    setChildAddressEmpty();
                    setEmptyItem(R.drawable.ic_empty, "No Address", "No address.Please add address.");
                    linear_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setChildAddressEmpty() {
        ref.child("Users").child(Uid).child("address").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void updateChildAddress(final Address address) {
        final String convertAd = convertAddressForm(address);
        Log.d(TAG, "updateChildAddress: "+address.isUsability());
        Query query = ref.child("Address_Books").child(Uid).orderByChild("usability").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ref.child("Users").child(Uid).child("address").setValue(convertAd).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                        }
                    });
                }else{
                    HashMap<String,Object> usability = new HashMap<String, Object>();
                    usability.put("usability",true);
                    ref.child("Address_Books").child(Uid).child(address.getKey()).updateChildren(usability).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: ");
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

    private String convertAddressForm(Address a){
        String T = "";
        String []ad = a.getAddress().split( ":");
        for(String i : ad)
            T += i+" ";
        return T.trim();
    }

    private void setEmptyItem(int image, String title, String desc) {
        image_no_item.setBackgroundResource(image);
        title_no_item.setText(title);
        desc_no_item.setText(desc);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.menu).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            startActivity(new Intent(AddressBooksActivity.this, AddAddressActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}