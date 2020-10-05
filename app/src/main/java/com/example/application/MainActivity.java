package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.application.Fragments_BottomNav.AccountFragment;
import com.example.application.Fragments_BottomNav.ChatFragment;
import com.example.application.Fragments_BottomNav.ListFragment;
import com.example.application.Fragments_BottomNav.SearchFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity implements AccountFragment.onFragmentBtnSelect {

    private Toolbar toolbar;

    private TextView title_no_item, desc_no_item;
    private LinearLayout linear_layout;
    private ImageView image_no_item;
    private FrameLayout container_fragments;
    private BottomNavigationView bottomNavigationView;

    private Dialog dialog;

    private DatabaseReference ref;
    private FirebaseAuth auth;
    private String Uid;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private int LOCATION_REQUEST_CODE = 10001;
    private HashMap<String, Double> Hlocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private boolean doubleBackToExitPressedOnce = false;
    private boolean loadFragments = true;

    private final Fragment searchFragment = new SearchFragment();
    private final Fragment chatFragment = new ChatFragment();
    private final Fragment listFragment = new ListFragment();
    private final Fragment accountFragment = new AccountFragment();

    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = searchFragment;

    private String cameraPermissions[];
    private String storagePermissions[];

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private Uri image_Uri;

    private static final String TAG = "MainActivity";

    /////////////////////////////*  ปุ่ม Back ของโทรศัพท์ จะต้องกด 2 ครั้งเพื่อออก  *//////////////////////////////////
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainActivity.this, "Please press back again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");

    //////////////////////////////////////* Toolbar *///////////////////////////////////////////////
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");


    ////////////////////////////////////*  Setting Location GPS    *////////////////////////////////
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    ///////////////////////*    กำหนด permission ของ Camera และ Gallery *///////////////////////////////
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ////////////////////////////*   การอ้างอิง Object View ใน Layout XML นั้นๆ  *////////////////////////////
        container_fragments = findViewById(R.id.container_fragments);
        linear_layout = findViewById(R.id.linear_layout);
        image_no_item = findViewById(R.id.image_no_item);
        title_no_item = findViewById(R.id.title_no_item);
        desc_no_item = findViewById(R.id.desc_no_item);
        bottomNavigationView = findViewById(R.id.bottomNav);

    ////////////////////////////*   เชื่อมต่อ Authentication and Database *//////////////////////////////
        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

    ////////////////////////////* check ว่ามีผู้ใช้งานหรือไม่ ถ้าไม่มีให้กลับไป LoginActivity *//////////////////////////
        if(auth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

    //////////////////////////////////*  ถ้ามีผุ้ใช้งานให้ทำการ ดึง uid *////////////////////////////////////////
        Uid = auth.getCurrentUser().getUid();

    /////////////////////*  ดัก Action ของ BottomNav ว่าเลือก Item อะไร */////////////////////////////////////
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    /////////////////////////*  function disconnectFromFireBase *////////////////////////////////////
        disconnectFromFireBase();
    }

    ///////////////////////////* อธิบายที่ Comments 1/10/2020 19:00 *////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        if (isConnection()) {
            getSupportActionBar().show();
            if(loadFragments) {
                loadFragment();
                loadFragments = false;
            }
            container_fragments.setVisibility(View.VISIBLE);
            linear_layout.setVisibility(View.INVISIBLE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            } else {
                askLocationPermission();
            }
        } else {
            getSupportActionBar().hide();
            container_fragments.setVisibility(View.INVISIBLE);
            linear_layout.setVisibility(View.VISIBLE);
            setEmptyItem(R.drawable.ic_disconnected, "Disconnected", "No internet connection, please check your connection.");
        }
        if (auth.getCurrentUser() != null) {
            updateStatusOnline(true);
        }
    }

    //////////////////////* เมื่อ Activity Stop จะทำการ stop การ update location gps  */////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        stopLocationUpdates();
    }

    //-------------------------------------* ZONE FRAGMENTS *-------------------------------------//
    ///////////////////////////////////*  load Fragments *//////////////////////////////////////////
    private void loadFragment() {
        fm.beginTransaction().add(R.id.container_fragments, searchFragment, "1").commit();
        fm.beginTransaction().add(R.id.container_fragments, chatFragment, "2").hide(chatFragment).commit();
        fm.beginTransaction().add(R.id.container_fragments, listFragment, "3").hide(listFragment).commit();
        fm.beginTransaction().add(R.id.container_fragments, accountFragment, "4").hide(accountFragment).commit();
    }


    //////////////////////* ถ้าเลือก Item อะไรที่อยู่ใน BottomNav จะทำการ Show Fm หน้าที่เลือกขึ้นมา *//////////////////////
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search:
                    toolbar.setTitle("Search");
                    fm.beginTransaction().hide(active).show(searchFragment).commit();
                    active = searchFragment;
                    return true;

                case R.id.chat:
                    toolbar.setTitle("Chat");
                    fm.beginTransaction().hide(active).show(chatFragment).commit();
                    active = chatFragment;
                    return true;

                case R.id.list:
                    toolbar.setTitle("List");
                    fm.beginTransaction().hide(active).show(listFragment).commit();
                    active = listFragment;
                    return true;
                case R.id.account:
                    toolbar.setTitle("Account");
                    fm.beginTransaction().hide(active).show(accountFragment).commit();
                    active = accountFragment;
                    return true;
            }
            return false;
        }
    };


    //--------------------------------* ZONE LOCATION GPS *---------------------------------------//
    //////////////////////////////////* ขอสิทธิ์เข้าถึง Location GPS *//////////////////////////////////////
    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    /////////////*    Check ว่าเปิด GPS หรือยัง ถ้าเปิดแล้วจะ Call startLocationUpdates function  *///////////////
    private void checkSettingsAndStartLocationUpdates() {
        Log.d(TAG, "checkSettingsAndStartLocationUpdates: ");
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                if (locationSettingsResponse.getLocationSettingsStates().isGpsUsable()) {
                    Log.d(TAG, "onSuccess: Start");
                    startLocationUpdates();
                } else {
                    Log.d(TAG, "onSuccess: No start");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        startIntentSenderForResult(apiException.getResolution().getIntentSender(), LOCATION_REQUEST_CODE, null, 0, 0, 0, null);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    ////////////////////////////////*  Updates location GPS ของโทรศัทพ์  *///////////////////////////////
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "startLocationUpdates: ");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //////////////////////////////* หยุด Updates location GPS ของโทรศัทพ์  *///////////////////////////////
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /* เมื่อครบทุกๆเวลาที่ update location จะทำการ get location ขณะนั้นแล้ว call SaveLocation function เพื่อบันทึกลงในผู้ใช้งานคนดังกล่าว */
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null) {
                return;
            }
            List<Location> location = locationResult.getLocations();
            SaveLocation(location.get(0));
        }
    };

    //--------------------------* ZONE NETWORK AND GPS ENABLE *-----------------------------------//
    ////////////////////////////////* เชื่อมต่อ Internet อยู่หรือป่าว *////////////////////////////////////////
    private boolean isConnection(){
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }

    //////////////////////////////* setEmptyItem function ถ้าไม่มี net */////////////////////////////////
    private void setEmptyItem(int image, String title, String desc) {
        image_no_item.setBackgroundResource(image);
        title_no_item.setText(title);
        desc_no_item.setText(desc);
    }

    ////////////////////////////* show เมื่อมีการ denied การขอเข้าสิทธิ์ Location GPS *//////////////////////////
    private void ShowDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_false_gps);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btn_done = dialog.findViewById(R.id.btn_done);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //////////////////////////* ActivityResult คือดูว่าการตอบรับ ok หรือป่าว *///////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
                ShowDialog(MainActivity.this);
            }
        }
        if(requestCode == IMAGE_PICK_CAMERA_CODE && resultCode == RESULT_OK){// && data != null งงว่าทำไมโทรศัพท์จริงๆใช้ไม่ได้ ?
            CropImage.activity(image_Uri).setGuidelines(CropImageView.Guidelines.ON)
                                         .setAspectRatio(1,1)
                                         .start(MainActivity.this);

            Toast.makeText(this, "Camera: "+image_Uri, Toast.LENGTH_LONG).show();
        }
        if(requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK){// && data != null งงว่าทำไมโทรศัพท์จริงๆใช้ไม่ได้ ?
            image_Uri = data.getData();
            CropImage.activity(image_Uri).setGuidelines(CropImageView.Guidelines.ON)
                                         .setAspectRatio(1,1)
                                         .start(MainActivity.this);
            // Nice Code
            Toast.makeText(this, "Gallery: "+image_Uri, Toast.LENGTH_LONG).show();
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                image_Uri = result.getUri();

                File actualImage = new File(image_Uri.getPath());

                Bitmap compressedImage = new Compressor.Builder(this)
                    .setMaxWidth(255)
                    .setMaxHeight(250)
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToBitmap(actualImage);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                byte []final_image = byteArrayOutputStream.toByteArray(); // Final Image ที่ผ่านการ Compress แล้ว

                ((AccountFragment)accountFragment).UploadImage(final_image);

                Toast.makeText(this, "Image: "+image_Uri, Toast.LENGTH_LONG).show();

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, ""+result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //////////////////////////////* Request Permission ต่างๆ */////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                checkSettingsAndStartLocationUpdates();
                //getLastLocation();
            } else {
                //Permission not granted
                ShowDialog(MainActivity.this);
            }
        }
        if(requestCode ==  CAMERA_REQUEST_CODE){
            if(grantResults.length > 0){
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted && writeStorageAccepted){
                    pickFromCamera();
                }else{
                    Toast.makeText(this, "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == STORAGE_REQUEST_CODE){
            if(grantResults.length > 0){
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(writeStorageAccepted){
                    pickFromGallery();
                }else{
                    Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    ////////////////////*  บันทึก location ที่ได้ลงไปยัง database ของผู้ใช้งานคนดังกล่าว *////////////////////////////////
    private void SaveLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Hlocation = new HashMap<String, Double>();
        Hlocation.put("latitude", latitude);
        Hlocation.put("longitude", longitude);

        ref.child("Users").child(Uid).child("location").setValue(Hlocation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: SaveLocation");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    /////////////////////////////////////////* update สถานะ online *//////////////////////////////////
    private void updateStatusOnline(boolean status){
        HashMap<String, Object> onlineStatus = new HashMap<String, Object>();
        onlineStatus.put("online",status);
        ref.child("Users").child(Uid).updateChildren(onlineStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: YES");
                }
            }
        });
    }

    ////////////////////////*  ตัวสอบว่าเชื่อมต่อกับ Database ไหม ถ้าไม่ก็จะ update สถานะ offline *////////////////////
    private void disconnectFromFireBase() {
        ref.child("Users").child(Uid).child("online").onDisconnect().setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: disconnect successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: fail disconnect "+e.getMessage());
            }
        });
    }

    //////////////////////////////////////////////* ออกจากระบบ *////////////////////////////////////////
    @Override
    public void logout() {
        updateStatusOnline(false);
        auth.signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    /////////////////////////////////////* Interface ของการกดรูปที่หน้า fragment account *///////////////////
    @Override
    public void onButtonSelect() {
        showImagePickDialog();
    }

    //-------------------------------* ZONE CAMERA & GALLERY *------------------------------------//
    ///////////////////////////////*  Dialog ว่าจะเลือก Camera หรือ Gallery *//////////////////////////////
    private void showImagePickDialog() {
        String []option = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick image form");
        builder.setCancelable(true);
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    if(!checkCameraPermission())
                        requestCameraPermission();
                    else
                        pickFromCamera();
                }else{
                    if(!checkStoragePermission())
                        requestStoragePermission();
                    else
                        pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    /////////////////////////////////*      Camera (Check)     *////////////////////////////////////
    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    ////////////////////////////////*      Camera (Request)     *///////////////////////////////////
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
    }

    /////////////////////////////////*      Gallery (Check)     *////////////////////////////////////
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    ////////////////////////////////*      Gallery (Request)     *///////////////////////////////////
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    ///////////////////////////////////////*    เข้าถึง Camera  *////////////////////////////////////////
    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        image_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_Uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    ///////////////////////////////////////*    เข้าถึง Gallery  *////////////////////////////////////////
    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
}

/* Comments 1/10/2020 19:00 Check:True
    - onCreate:
        - setting location gps, bottomNav
    - onStart:
        - Check internet
            o กรณีที่มี internet จะทำการ load fragments ทั้งหมด
            o กรณีที่ไม่มี internet setEmptyItem function ว่าไม่ได้เชื่อมต่อ internet
        - Check location gps
            o กรณีที่มี GPS ก็จะใช้งานได้ตามปกติ
            o กรณีที่ไม่มี GPS จะ Show dialog ของ GPS ขึ้นมาเพื่อให้เปิด GPS
        ( สรุปจะ load fragments ขึ้นมาก็ต่อเมื่อมี internet )
    - onRestart:
        - ลบ fragments ที่มีทั้งหมด (ไม่มีก็ไม่ทำ)

    - location GPS:
        - ขอสิทธิ์เข้าถึง location GPS ก่อน
            o ถ้ายังไม่มีจะถามก่อน
                - Show dialog permission ว่าจะ accept หรือ denied
            o ถ้ามีก็จะไป Check location gps ว่าทำงานอยู่หรือป่าว
*/

//    private void getLastLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
//        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    //We have a location
//                    Log.d(TAG, "onSuccess: " + location.toString());
//                    Log.d(TAG, "onSuccess: " + location.getLatitude());
//                    Log.d(TAG, "onSuccess: " + location.getLongitude());
//                } else  {
//                    Log.d(TAG, "onSuccess: Location was null...");
//                }
//            }
//        });
//        locationTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
//            }
//        });
//    }


//                Fragment fragment = null;
//                switch (item.getItemId()){
//                    case R.id.search:
//                        fragment = new SearchFragment();
//                        setTextToolbar("Search","Find a housekeeper near you");
//                        break;
//                    case R.id.chat:
//                        fragment = new ChatFragment();
//                        setTextToolbar("Chat","Contact for more information");
//                        break;
//                    case R.id.list:
//                        fragment = new ListFragment();
//                        setTextToolbar("List","All executed items");
//                        break;
//                    case R.id.account:
//                        fragment = new AccountFragment();
//                        setTextToolbar("Account","Your account");
//                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
//                return true;