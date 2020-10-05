package com.example.application.Fragments_BottomNav;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Adapter.SearchRecyclerAdapter;
import com.example.application.Adapter.SumPostDistance;
import com.example.application.LoginActivity;
import com.example.application.R;
import com.example.application.Retrieving_Data.Locations;
import com.example.application.Retrieving_Data.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class SearchFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout linear_layout;
    private ImageView image_no_item;
    private TextView title_no_item, desc_no_item;

    private int DistanceUser;

    private FirebaseAuth auth;
    private DatabaseReference ref;

    private List<SumPostDistance> listSumPostDistances;
    private SearchRecyclerAdapter searchRecyclerAdapter;

    private String Uid;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private boolean gps_enabled;
    private LocationManager locationManager;

    private static final String TAG = "SearchFragment";

    private static int REFRESH = 3000;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater inflater) {
        Query query = ref.child("Users").child(Uid).child("position");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    inflater.inflate(R.menu.menu_toolbar, menu);
                    menu.findItem(R.id.menu).setVisible(false);
                    menu.findItem(R.id.settings).setVisible(false);
                    if (snapshot.getValue().equals("user")) {
                        menu.findItem(R.id.add).setVisible(false);
                        getDistanceFromAccount();
                    } else {
                        menu.findItem(R.id.add).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.filter) {
            Toast.makeText(getActivity(), "Filter", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listSumPostDistances = new ArrayList<SumPostDistance>();

        swipeRefreshLayout = view.findViewById(R.id.swipeRef);
        recyclerView = view.findViewById(R.id.recycler_view);

        linear_layout = view.findViewById(R.id.linear_layout);
        image_no_item = view.findViewById(R.id.image_no_item);
        title_no_item = view.findViewById(R.id.title_no_item);
        desc_no_item = view.findViewById(R.id.desc_no_item);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        searchRecyclerAdapter = new SearchRecyclerAdapter(getContext(), listSumPostDistances);
        recyclerView.setAdapter(searchRecyclerAdapter);

        Uid = auth.getCurrentUser().getUid();

        QueryLocationMe();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Refresh();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, REFRESH);
            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    private void Refresh() {
        if(!isConnection() || !isGps_enabled()){
            Intent intent = getActivity().getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().overridePendingTransition(0, 0);
            getActivity().finish();

            getActivity().overridePendingTransition(0, 0);
            startActivity(intent);
        }else{
            linear_layout.setVisibility(View.INVISIBLE);
            QueryLocationMe();
        }
    }

    private void setEmptyItem(int image, String title, String desc) {
        image_no_item.setBackgroundResource(image);
        title_no_item.setText(title);
        desc_no_item.setText(desc);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isConnection(){
        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }

    private boolean isGps_enabled(){
        return gps_enabled = locationManager.isProviderEnabled(LocationManager. GPS_PROVIDER);
    }


    private void QueryLocationMe() {
        Log.d(TAG, "QueryLocationUser: ");
        Query query = ref.child("Users").child(Uid).child("location");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Locations locations = snapshot.getValue(Locations.class);
                    QueryPostMaid(locations);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void QueryPostMaid(final Locations locations1) {
        Query query1 = ref.child("Post").orderByChild("uID");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    listSumPostDistances.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        final Post post = ds.getValue(Post.class);
                        final int DistanceMaid = (post.getDistance().equals("everywhere")) ? Integer.MAX_VALUE : Integer.parseInt(post.getDistance());
                        Log.d(TAG, "onDataChange: 1 "+post.getuID());
                        Query query2 = ref.child("Users").child(post.getuID()).child("location");
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Locations locations2 = snapshot.getValue(Locations.class);
                                    double distance = Calculate_Distance(locations1,locations2);
                                    if((int)Math.ceil(distance) <= DistanceUser && (int)Math.ceil(distance) <= DistanceMaid) {
                                        SumPostDistance sumPostDistance = new SumPostDistance(post, distance);
                                        listSumPostDistances.add(sumPostDistance);
                                    }
//                                    Log.d(TAG, "onDataChange: "+sumPostDistance.getPost().getProfile_Url());
                                }
                                if(listSumPostDistances.isEmpty()){
                                    setEmptyItem(R.drawable.ic_empty, "No Post", "No post,There are no posts you want.");
                                    linear_layout.setVisibility(View.VISIBLE);
                                }else{
                                    linear_layout.setVisibility(View.INVISIBLE);
                                }
                                Collections.sort(listSumPostDistances);
                                searchRecyclerAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });
                    }
                }
                else {
                    setEmptyItem(R.drawable.ic_empty, "No Post", "No post");
                    linear_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    private void getDistanceFromAccount(){
        Query getDistanceFromAccount = ref.child("Users").child(Uid).child("distance");
        getDistanceFromAccount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DistanceUser = snapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double Calculate_Distance(Locations x, Locations y){
        BigDecimal bd;
        double lat1 = Math.toRadians(x.getLatitude());
        double long1 = Math.toRadians(x.getLongitude());

        double lat2 = Math.toRadians(y.getLatitude());
        double long2 = Math.toRadians(y.getLongitude());

        double dlat = lat2 - lat1;
        double dlong = long2 - long1;

        double ans = Math.pow(Math.sin(dlat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlong/2), 2);
        double KM = 2 * Math.asin(Math.sqrt(ans));

        bd = new BigDecimal(KM * 6378.8).setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}

/*  22/09/20 0:32
        QueryLocationUser();
        listSumPostDistances.clear();;
        if(checkNetworkConnectStatus()){
            show_post = true;
            checkSettingsAndStartLocationUpdates();
        } else{
            Toast.makeText(getActivity(), "No Internet.", Toast.LENGTH_SHORT).show();
        }

 */

/*

          ใช้นะ  22/09/20 0:32     //
        btn_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> x = new HashMap<String, Object>();
                x.put("desc","Test Add Post.");
                x.put("distance","everywhere");
                x.put("email","example@gmail.com");
                x.put("name","Golf");
                x.put("post_Url","gs://application-64977.appspot.com/Image/VZpY7wbIBfMBuIavWUqZ/96824377_637827040280202_3807954975712083968_n.jpg");
                x.put("price","500");
                x.put("profile_Url","gs://application-64977.appspot.com/Image/VZpY7wbIBfMBuIavWUqZ/96824377_637827040280202_3807954975712083968_n.jpg");
                x.put("timestamp","current_time");
                x.put("title","Test Add Post.");
                x.put("uID","VZpY7wbIBfMBuIavWUqZ");

                ref.child("Post").child("-ikme547sjp084nzakq1k").setValue(x).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: YES");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Refresh();
                                }
                            }, REFRESH);
                        }
                    }
                });

            }
        });

 */

/* 22/09/20 0:32
    Query query1 = ref.child("Post").orderByChild("uID");
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    listSumPostDistances.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        final Post post = ds.getValue(Post.class);
                        final int DistanceMaid = (post.getDistance().equals("everywhere")) ? Integer.MAX_VALUE : Integer.parseInt(post.getDistance());
                        Log.d(TAG, "onDataChange: 1 "+post.getuID());
                        Query query2 = ref.child("Users").child(post.getuID()).child("location");
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Locations locations2 = snapshot.getValue(Locations.class);
                                    double distance = Calculate_Distance(locations1,locations2);
                                    if((int)Math.ceil(distance) <= DistanceUser && (int)Math.ceil(distance) <= DistanceMaid) {
                                        SumPostDistance sumPostDistance = new SumPostDistance(post, distance);
                                        listSumPostDistances.add(sumPostDistance);
                                    }
//                                    Log.d(TAG, "onDataChange: "+sumPostDistance.getPost().getProfile_Url());
                                }
                                if(listSumPostDistances.isEmpty()){
                                    setEmptyItem(R.drawable.ic_empty, "No Post", "No post,There are no posts you want.");
                                    linear_layout.setVisibility(View.VISIBLE);
                                }else{
                                    linear_layout.setVisibility(View.INVISIBLE);
                                }
                                Collections.sort(listSumPostDistances);
                                searchRecyclerAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });
                    }
                }
                else {
                    setEmptyItem(R.drawable.ic_empty, "No Post", "No post");
                    linear_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
 */

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        data = new ArrayList<Post>();
//
//        swipeRefreshLayout = view.findViewById(R.id.swipeRef);
//        recyclerView = view.findViewById(R.id.my_recycler_view);
//        emptyRecycler = view.findViewById(R.id.empty_recycler);
//
//        searchRecyclerAdapter = new SearchRecyclerAdapter(getContext(), data);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(searchRecyclerAdapter);
//
//        ////////////////////////////////////////////////////////////
//
//        auth = FirebaseAuth.getInstance();
//        Uid = auth.getCurrentUser().getUid();
//
//        ref = FirebaseDatabase.getInstance().getReference();
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getActivity().finish();
//                getActivity().overridePendingTransition(0, 0);
//                getActivity().startActivity(getActivity().getIntent());
//                getActivity().overridePendingTransition(0, 0);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, REFRESH);
//            }
//        });
//
//    }
//    LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult == null) {
//                return;
//            }
//            List<Location> location = locationResult.getLocations();
//            SaveLocation(location);
//        }
//    };
//

//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        checkSettingsAndStartLocationUpdates();
//    }
//
//    private void startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//
//    }
//
//    private void stopLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }
//
//
//    private void checkSettingsAndStartLocationUpdates() {
//        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest).build();
//        SettingsClient client = LocationServices.getSettingsClient(getContext());
//
//        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
//        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                //Settings of device are satisfied and we can start location updates
//                startLocationUpdates();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    ResolvableApiException apiException = (ResolvableApiException) e;
//                    try {
////                        apiException.startResolutionForResult(getActivity(), LOCATION_REQUEST_CODE);
//                        startIntentSenderForResult(apiException.getResolution().getIntentSender(), LOCATION_REQUEST_CODE, null, 0, 0, 0, null);
//                    } catch (IntentSender.SendIntentException ex) {
//                        ex.printStackTrace();
//
//                    }
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == LOCATION_REQUEST_CODE){
//            if(resultCode == Activity.RESULT_OK){
//                startLocationUpdates();
//            }else{
//                stopLocationUpdates();
//                ShowDialog(getContext());
//            }
//        }
//    }
//


//    private void CreatedPost(final Locations locations){
//        Query query1 = ref.child("Post");
//        query1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    Post post = snapshot.getValue(Post.class);
//                    String Uid = post.getuID();
//                    Log.d(TAG, "onChildAdded: "+Uid);
//                    Query query2 = ref.child("Users").child(post.getuID());
//                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.exists()) {
//                                Log.d("BBBBBBBBBBBBBB", "onDataChange: "+snapshot.getValue());
//                                ProfileMaidData profileMaidData = snapshot.getValue(ProfileMaidData.class);
//                                String name = profileMaidData.getName();
//                                String profile_Url = profileMaidData.getProfile_Url();
//                                double space = Calculate_Distance(locations, profileMaidData);
//                                post.setName(name);
//                                post.setProfile_Url(profile_Url);
//                                post.setSpace(space);
//                                data.add(post);
//                            }
//                            Collections.sort(data);
//                            searchRecyclerAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d("AAAAAAAAAAA", "onChildChanged: ");
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Log.d("AAAAAAAAAAA", "onChildRemoved: ");
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d("AAAAAAAAAAA", "onChildMoved: ");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("AAAAAAAAAAA", "onCancelled: ");
//            }
//        });
//    }

//    Query query2 = ref.child("Users").child(post.getuID());
//                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.exists()) {
//                                Log.d("BBBBBBBBBBBBBB", "onDataChange: "+snapshot.getValue());
//                                ProfileMaidData profileMaidData = snapshot.getValue(ProfileMaidData.class);
//                                String name = profileMaidData.getName();
//                                String profile_Url = profileMaidData.getProfile_Url();
//                                double space = Calculate_Distance(locations, profileMaidData);
//                                post.setName(name);
//                                post.setProfile_Url(profile_Url);
//                                post.setSpace(space);
//                                data.add(post);
//                            }
//                            searchRecyclerAdapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
