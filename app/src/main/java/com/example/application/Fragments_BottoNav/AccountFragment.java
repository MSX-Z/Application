package com.example.application.Fragments_BottoNav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.EditAddressBooksActivity;
import com.example.application.EditProfileActivity;
import com.example.application.IntroActivity;
import com.example.application.LoginActivity;
import com.example.application.MainActivity;
import com.example.application.R;
import com.example.application.Retrieving_Data.ProfileUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class AccountFragment extends Fragment {

    private CircularImageView profile;
    private TextView name, address, email, phone;
    private DiscreteSeekBar discreteSeekBar;
    private Button user_info,address_books,btn_logout;
    private ProgressDialog progressDialog;
    private static int SPLASH_TIME_OUT = 3000;

    private DatabaseReference ref;
    private FirebaseAuth auth;
    private String Uid;

    private static final String TAG = "AccountFragment";

    private int Distance;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);

        user_info = view.findViewById(R.id.user_info);
        address_books = view.findViewById(R.id.address_books);
        discreteSeekBar = view.findViewById(R.id.discrete_seek_bar);
        btn_logout = view.findViewById(R.id.btn_logout);
        progressDialog = new ProgressDialog(getContext());

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(),LoginActivity.class));
            getActivity().finish();
        }

        Query getDistance = ref.child("Users").child(Uid).child("distance");
        getDistance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Distance = snapshot.getValue(Integer.class);
                    discreteSeekBar.setProgress(Distance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query updateInfoUser = ref.child("Users").child(Uid);
        updateInfoUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ProfileUserData profileUserData = snapshot.getValue(ProfileUserData.class);
                    setInfoUser(profileUserData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditProfileActivity.class));
            }
        });
        
        address_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),EditAddressBooksActivity.class));
            }
        });

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(fromUser){
                    Distance = value;
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: ");
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                ref.child("Users").child(Uid).child("distance").setValue(Distance).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: ");
                        }
                    }
                });
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Goodbye See you again...");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Logout();
                    }
                },SPLASH_TIME_OUT);
            }
        });

    }

    private void setInfoUser(ProfileUserData profileUserData){
        name.setText(profileUserData.getName());
        address.setText(profileUserData.getAddress());
        email.setText(profileUserData.getEmail());
        phone.setText(profileUserData.getPhone());

    }

    private void Logout(){
        auth.signOut();
        startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

}