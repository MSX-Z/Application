package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.Test_Program.Test;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;
import java.util.Locale;

public class RegisterMaidActivity extends AppCompatActivity implements LocationListener {

    private ImageButton btn_back, btn_gps;
    private CircularImageView profile;
    private RoundedImageView image_id_card;
    private EditText name, id_card, birth, address, county, state, city, number_under_image, email, password, con_password;
    private Button btn_register;
    private AVLoadingIndicatorView avi;

    private Uri image_uri_profile,image_uri_id_card;

    private String EmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String NumberUnderImagePattern = "^\\d{4}-\\d{2}-\\d{8}$";
    private String ID_CardPattern = "^\\d{1}-\\d{4}-\\d{5}-\\d{2}-\\d{1}$";
    private String BirthPattern = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLEY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private double latitude, longitude;

    private LocationManager locationManager;

    private boolean left_true_right_false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_maid);

        btn_back = findViewById(R.id.btn_back);
        btn_gps = findViewById(R.id.btn_gps);

        profile = findViewById(R.id.profile);
        image_id_card = findViewById(R.id.image_id_card);

        name = findViewById(R.id.name);
        id_card = findViewById(R.id.id_card);
        birth = findViewById(R.id.birth);

        address = findViewById(R.id.address);
        county = findViewById(R.id.county);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);

        number_under_image = findViewById(R.id.number_under_image);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.con_password);

        btn_register = findViewById(R.id.btn_register);

        avi = findViewById(R.id.loading);

        ////////////////////////////////////////////////////////////

        name.addTextChangedListener(textWatcher);
        id_card.addTextChangedListener(textWatcher);
        birth.addTextChangedListener(textWatcher);
        address.addTextChangedListener(textWatcher);
        county.addTextChangedListener(textWatcher);
        state.addTextChangedListener(textWatcher);
        city.addTextChangedListener(textWatcher);
        number_under_image.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        con_password.addTextChangedListener(textWatcher);

        ////////////////////////////////////////////////////////////

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ////////////////////////////////////////////////////////////


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                if (checkLocationPermission()) {
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left_true_right_false = true;
                showImagePickDialog();
            }
        });

        image_id_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left_true_right_false = false;
                showImagePickDialog();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String ID_Card = id_card.getText().toString().trim();
                String Birth = birth.getText().toString().trim();
                String Address = address.getText().toString().trim();
                String County = county.getText().toString().trim();
                String State = state.getText().toString().trim();
                String City = city.getText().toString().trim();
                String Number_Under_Image = number_under_image.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Confirm_Password = con_password.getText().toString().trim();

                PassingImage();
//                if(image_uri_profile == null){
//                    Toast.makeText(RegisterMaidActivity.this, "Please add a face image to identify yourself.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else if(image_uri_id_card == null){
//                    Toast.makeText(RegisterMaidActivity.this, "Please enter a picture of identification to identify you.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                else if (ID_Card.length()-4 != 13) {
//                    id_card.setError("Identification number must be 13 digits.");
//                    return;
//                }
//                else if (!ID_Card.matches(ID_CardPattern)) {
//                    id_card.setError("Invalid identification.");
//                    return;
//                }
//                else if(!Birth.matches(BirthPattern)){
//                    birth.setError("Invalid birthday");
//                    return;
//                }
//                else if (Number_Under_Image.length()-2 != 14) {
//                    number_under_image.setError("Number under the picture must be 14 digits.");
//                    return;
//                }
//                else if (!Number_Under_Image.matches(NumberUnderImagePattern)) {
//                    number_under_image.setError("Invalid number under the picture.");
//                    return;
//                }
//                else if (!Email.matches(EmailPattern)) {
//                    email.setError("Invalid email address.");
//                    return;
//                }
//                else if (Password.length() < 8) {
//                    password.setError("Password must be longer than 8 character.");
//                    return;
//                }
//                else if (Confirm_Password.length() < 8) {
//                    con_password.setError("Confirm Password must be longer than 8 character.");
//                    return;
//                }
//                else if (!Password.equals(Confirm_Password)) {
//                    con_password.setError("Confirm password must match the password.");
//                    return;
//                }
//                else {
//
//                }
            }
        });
    }

    private void PassingImage() {
        Intent intent = new Intent(RegisterMaidActivity.this, Test.class);
        intent.putExtra("Profile",image_uri_profile);
        intent.putExtra("ID Card",image_uri_id_card);
        startActivity(intent);
    }

    // dialog choose camera or galley

    private void showImagePickDialog() {
        String option[] = {"Camera","Galley"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick up image").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //click camera
                    if(checkCameraPermission()){
                        pickFromCamera();
                    }
                    else{
                        requestCameraPermission();
                    }
                }
                else{
                    //click galley
                    if(checkStoragePermission()){
                        pickFromGalley();
                    }
                    else{
                        requestStoragePermission();
                    }
                }
            }
        }).show();
    }

    // put image from galley

    private void pickFromGalley(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLEY_CODE);
    }

    // put image from camera

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image Tile");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(left_true_right_false) {
            image_uri_profile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri_profile);
        }else{
            image_uri_id_card = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri_id_card);
        }
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    // Storage Write Permission

    private boolean checkStoragePermission(){
        boolean result = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    // Camera Permission

    private boolean checkCameraPermission(){
        boolean result1 = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    // GPS Location Permissions;

    private boolean checkLocationPermission() {
        boolean result = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    // Detect location

    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        // GPS location disable
        Toast.makeText(this, "Please turn on location ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //location detect
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();
    }

    private void findAddress() {
        // find address county,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            String County = addresses.get(0).getCountryName();
            String State = addresses.get(0).getAdminArea();
            String City = addresses.get(0).getLocality();
            String Address = addresses.get(0).getAddressLine(0);

            county.setText(County);
            state.setText(State);
            city.setText(City);
            address.setText(Address);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Name = name.getText().toString().trim();
            String ID_Card = id_card.getText().toString().trim();
            String Birth = birth.getText().toString().trim();
            String Address = address.getText().toString().trim();
            String County = county.getText().toString().trim();
            String State = state.getText().toString().trim();
            String City = city.getText().toString().trim();
            String Number_Under_Image = number_under_image.getText().toString().trim();
            String Email = email.getText().toString().trim();
            String Password = password.getText().toString().trim();
            String Confirm_Password = con_password.getText().toString().trim();

            btn_register.setEnabled(
                    !Name.isEmpty() && !ID_Card.isEmpty()
                 && !Birth.isEmpty() && !Address.isEmpty()
                 && !County.isEmpty() && !State.isEmpty()
                 && !City.isEmpty() && !Number_Under_Image.isEmpty()
                 && !Email.isEmpty() && !Password.isEmpty()
                 && !Confirm_Password.isEmpty()
            );
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = (grantResults[0]) == (PackageManager.PERMISSION_GRANTED);
                    if (locationAccepted) {
                        //permission allowed
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = (grantResults[0]) == (PackageManager.PERMISSION_GRANTED);
                    boolean storageAccepted = (grantResults[1]) == (PackageManager.PERMISSION_GRANTED);
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Camera Permissions are Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = (grantResults[0]) == (PackageManager.PERMISSION_GRANTED);
                    if (storageAccepted) {
                        //permission allowed
                        pickFromGalley();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Storage Permission is Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLEY_CODE){
                if(left_true_right_false){
                    image_uri_profile = data.getData();
                    profile.setImageURI(image_uri_profile);
                }
                else{
                    image_uri_id_card = data.getData();
                    image_id_card.setImageURI(image_uri_id_card);
                }
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                if(left_true_right_false) {
                    profile.setImageURI(image_uri_profile);
                }
                else{
                    image_id_card.setImageURI(image_uri_id_card);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
