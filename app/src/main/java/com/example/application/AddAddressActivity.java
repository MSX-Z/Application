package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Retrieving_Data.Address;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txt_toolbar;
    private Button btn_cancel,btn_delete_address,btn_save_address;

    private EditText name,phone,house_no_road_alley,sub_district,district,county,postal;

    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    private String Uid;
    private DatabaseReference ref;

    private String keyObject = null;
    private Address addressOfKeyObject = null;

    private static final String TAG = "AddAddressActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        txt_toolbar = findViewById(R.id.txt_toolbar);

        btn_cancel = findViewById(R.id.btn_cancel);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        house_no_road_alley = findViewById(R.id.house_no_road_alley);
        sub_district = findViewById(R.id.sub_district);
        district = findViewById(R.id.district);
        county = findViewById(R.id.county);
        postal = findViewById(R.id.postal);

        btn_delete_address = findViewById(R.id.btn_delete_address);

        btn_save_address = findViewById(R.id.btn_save_address);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(AddAddressActivity.this, LoginActivity.class));
            finish();
        }

        Uid = auth.getCurrentUser().getUid();

        Intent intent = getIntent();
        if(intent.hasExtra("selectedItem")){
            txt_toolbar.setText("Edit address");
            addressOfKeyObject = intent.getParcelableExtra("selectedItem");
            setEditText(addressOfKeyObject);
            btn_delete_address.setVisibility(View.VISIBLE);
        }else{
            txt_toolbar.setText("Add new address");
            keyObject = null;
            btn_delete_address.setVisibility(View.GONE);
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_delete_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyObject != null){
                    showDialogConfirm();
                }
            }
        });

        btn_save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Phone = phone.getText().toString().trim();
                String House_no_road_alley = house_no_road_alley.getText().toString().trim();
                String Sub_district = sub_district.getText().toString().trim();
                String District = district.getText().toString().trim();
                String County = county.getText().toString().trim();
                String Postal = postal.getText().toString().trim();

                if(TextUtils.isEmpty(Name)){
                    name.setError("Name required.");
                    return;
                }
                else if(TextUtils.isEmpty(Phone)){
                    phone.setError("Phone required.");
                    return;
                }
                else if(Phone.length() < 9){
                    phone.setError("Phone must be longer than 9-10 character.");
                    return;
                }
                else if(TextUtils.isEmpty(House_no_road_alley)){
                    house_no_road_alley.setError("House no./Road/Alley required.");
                    return;
                }
                else if(House_no_road_alley.contains(":")){
                    house_no_road_alley.setError("Avoid using letters ' : '");
                    return;
                }
                else if(TextUtils.isEmpty(Sub_district)){
                    sub_district.setError("Sub district required.");
                    return;
                }
                else if(Sub_district.contains(":")){
                    sub_district.setError("Avoid using letters ' : '");
                    return;
                }
                else if(TextUtils.isEmpty(District)){
                    district.setError("District required.");
                    return;
                }
                else if(District.contains(":")){
                    district.setError("Avoid using letters ' : '");
                    return;
                }
                else if(TextUtils.isEmpty(County)){
                    county.setError("County required.");
                    return;
                }
                else if(County.contains(":")){
                    county.setError("Avoid using letters ' : '");
                    return;
                }
                else if(TextUtils.isEmpty(Postal)){
                    postal.setError("Postal required.");
                    return;
                }
                else{
                    progressDialog.setMessage("Saving address...");
                    progressDialog.show();
                    if(keyObject == null){
                        checkAddressUpdateUsability(Name,Phone,House_no_road_alley,Sub_district,District,County,Postal);
                    }else{
                        addAddress(Name,Phone,House_no_road_alley,Sub_district,District,County,Postal);
                    }
                }
            }
        });

    }

    private void showDialogConfirm() {
        String addressMessage = convertAddressForm(addressOfKeyObject);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Are you sure you deleted your address ?");
        alertDialog.setMessage(addressMessage);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Deleting...");
                progressDialog.show();
                ref.child("Address_Books").child(Uid).child(keyObject).removeValue();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                        progressDialog.dismiss();
                    }
                },1000);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void setEditText(Address ad) {
        keyObject = ad.getKey();
        String []arrAddress = ad.getAddress().split(":");

        name.setText(ad.getName());
        phone.setText(ad.getPhone());
        house_no_road_alley.setText(arrAddress[0]);
        sub_district.setText(arrAddress[1]);
        district.setText(arrAddress[2]);
        county.setText(arrAddress[3]);
        postal.setText(arrAddress[4]);

    }

    private void checkAddressUpdateUsability(final String name, final String phone, final String house_no_road_alley, final String sub_district, final String district, final String county, final String postal) {
        Query query = ref.child("Address_Books").child(Uid).orderByChild("usability").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        ds.getRef().child("usability").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.setMessage("Update address...");
                                Log.d(TAG, "onSuccess: ");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddAddressActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onFailure: "+e.getMessage());
                            }
                        });
                    }
                }
                addAddress(name,phone,house_no_road_alley,sub_district,district,county,postal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addAddress(String name, String phone, String house_no_road_alley, String sub_district, String district, String county, String postal) {
        HashMap<String,Object> address = new HashMap<String, Object>();

        String Address = house_no_road_alley+":"+sub_district+":"+district+":"+county+":"+postal;
        String timestamp = String.valueOf(System.currentTimeMillis());
        String key = (keyObject == null) ? ref.push().getKey() : keyObject;
        boolean usability = (addressOfKeyObject == null) ? true : addressOfKeyObject.isUsability();

        address.put("name",name);
        address.put("phone",phone);
        address.put("address",Address);
        address.put("usability",usability);
        address.put("key",key);
        address.put("timestamp",timestamp);
        ref.child("Address_Books").child(Uid).child(key).setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: saveAddress");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                        progressDialog.dismiss();
                    }
                },1000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddAddressActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
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
}