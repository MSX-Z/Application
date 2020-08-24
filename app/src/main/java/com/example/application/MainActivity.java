package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.application.Fragments_BottoNav.AccountFragment;
import com.example.application.Fragments_BottoNav.ChatFragment;
import com.example.application.Fragments_BottoNav.ListFragment;
import com.example.application.Fragments_BottoNav.SearchFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView title, subtitle, title_no_item, desc_no_item;
    private LinearLayout linear_layout;
    private ImageView image_no_item;
    private FrameLayout container_fragments;
    private BottomNavigationView bottomNavigationView;
    private static final String TAG = "MainActivity";
    private int LOCATION_REQUEST_CODE = 10001;
    private Dialog dialog;

    private boolean loadFragments;

    private DatabaseReference ref;
    private FirebaseAuth auth;
    private String Uid;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private HashMap<String, Double> Hlocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private HashMap<String, Object> onlineStatus;

    private boolean doubleBackToExitPressedOnce = false;

    final Fragment searchFragment = new SearchFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment listFragment = new ListFragment();
    final Fragment accountFragment = new AccountFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        ////////////////////////////////////////////////////////////////////////////////////////////

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);

        container_fragments = findViewById(R.id.container_fragments);

        linear_layout = findViewById(R.id.linear_layout);
        image_no_item = findViewById(R.id.image_no_item);
        title_no_item = findViewById(R.id.title_no_item);
        desc_no_item = findViewById(R.id.desc_no_item);

        bottomNavigationView = findViewById(R.id.bottomNav);

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        loadFragments = true;

        onlineStatus = new HashMap<String, Object>();
        ////////////////////////////////////////////////////////////////////////////////////////////

        setTextToolbar("Search", "Find a housekeeper near you");

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        bottomNavigationView.setSelectedItemId(R.id.search);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search:
                    setTextToolbar("Search", "Find a housekeeper near you");
                    fm.beginTransaction().hide(active).show(searchFragment).commit();
                    active = searchFragment;
                    return true;

                case R.id.chat:
                    setTextToolbar("Chat", "Contact for more information");
                    fm.beginTransaction().hide(active).show(chatFragment).commit();
                    active = chatFragment;
                    return true;

                case R.id.list:
                    setTextToolbar("List", "All executed items");
                    fm.beginTransaction().hide(active).show(listFragment).commit();
                    active = listFragment;
                    return true;
                case R.id.account:
                    setTextToolbar("Account", "User related information");
                    fm.beginTransaction().hide(active).show(accountFragment).commit();
                    active = accountFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isConnection()) {
                checkSettingsAndStartLocationUpdates();
                container_fragments.setVisibility(View.VISIBLE);
                linear_layout.setVisibility(View.INVISIBLE);
            } else {
                container_fragments.setVisibility(View.INVISIBLE);
                linear_layout.setVisibility(View.VISIBLE);
                setEmptyItem(R.drawable.ic_disconnected, "Disconnected", "No internet connection, please check your connection.");
            }
        } else {
            askLocationPermission();
        }
        if (auth.getCurrentUser() != null) {
            updateStatusOnline(true);
        }
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
                ShowDialog(MainActivity.this);
            }
        }
    }

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

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

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
                if(loadFragments){
                    loadFragment();
                    loadFragments = false;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void loadFragment() {
        fm.beginTransaction().add(R.id.container_fragments, searchFragment, "1").commit();
        fm.beginTransaction().add(R.id.container_fragments, chatFragment, "2").hide(chatFragment).commit();
        fm.beginTransaction().add(R.id.container_fragments, listFragment, "3").hide(listFragment).commit();
        fm.beginTransaction().add(R.id.container_fragments, accountFragment, "4").hide(accountFragment).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        updateStatusOnline(false);
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        updateStatusOnline(false);
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                checkSettingsAndStartLocationUpdates();
//                getLastLocation();
            } else {
                //Permission not granted
                ShowDialog(MainActivity.this);
            }
        }
    }

    private boolean isConnection(){
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }

    private void setTextToolbar(String title,String subtitle){
        this.title.setText(title);
        this.subtitle.setText(subtitle);
    }

    private void setEmptyItem(int image, String title, String desc) {
        image_no_item.setBackgroundResource(image);
        title_no_item.setText(title);
        desc_no_item.setText(desc);
    }

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

    private void updateStatusOnline(boolean status){
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

    private void Logout(){
        auth.signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
    
}


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