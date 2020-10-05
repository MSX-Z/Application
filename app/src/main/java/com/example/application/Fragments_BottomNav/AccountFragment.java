package com.example.application.Fragments_BottomNav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.application.Adapter.PostMeRecyclerAdapter;
import com.example.application.Adapter.SearchRecyclerAdapter;
import com.example.application.AddressBooksActivity;
import com.example.application.Retrieving_Data.AccountData;
import com.example.application.UserInformationActivity;
import com.example.application.LoginActivity;
import com.example.application.R;
import com.example.application.Retrieving_Data.ProfileUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class AccountFragment extends Fragment {

    private CardView area_discrete_seek_bar;

    private CircularImageView profile;
    private TextView name, address, email, phone;
    private DiscreteSeekBar discreteSeekBar;
    private RecyclerView container_post_me;
    private Button user_info,address_books,btn_logout;
    private ProgressDialog progressDialog;

    private PostMeRecyclerAdapter postMeRecyclerAdapter;

    private DatabaseReference ref;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    private String Uid,Position;

    private AccountData accountData;

    private onFragmentBtnSelect listener;

    private static int SPLASH_TIME_OUT = 3000;

    private static final String TAG = "AccountFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.menu).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.settings)
            Toast.makeText(getActivity(), "settings", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof onFragmentBtnSelect){
            listener = (onFragmentBtnSelect) context;
        }else{
            throw new ClassCastException(context.toString()+" must implement listener");
        }
    }

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

        area_discrete_seek_bar = view.findViewById(R.id.area_discrete_seek_bar);

        profile = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);

        user_info = view.findViewById(R.id.user_info);
        address_books = view.findViewById(R.id.address_books);
        discreteSeekBar = view.findViewById(R.id.discrete_seek_bar);
        container_post_me = view.findViewById(R.id.container_post_me);
        btn_logout = view.findViewById(R.id.btn_logout);
        progressDialog = new ProgressDialog(getContext());

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference(); // นำไปใส่ที่ AccountFragment

        if(auth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(),LoginActivity.class));
            getActivity().finish();
        }

        Uid = auth.getCurrentUser().getUid();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        container_post_me.setLayoutManager(manager);
        container_post_me.setHasFixedSize(true);
        postMeRecyclerAdapter = new PostMeRecyclerAdapter(getContext());
        container_post_me.setAdapter(postMeRecyclerAdapter);

        queryUpdateProfileData();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelect();
                Toast.makeText(getActivity(), "profile name: "+accountData.getProfile_Url(), Toast.LENGTH_SHORT).show();
            }
        });

        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserInformationActivity.class));
            }
        });
        
        address_books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddressBooksActivity.class));
            }
        });

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            int Distance;
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
                    listener.logout();
                }
            },SPLASH_TIME_OUT);
            }
        });

    }

    private void queryUpdateProfileData(){
        Query query = ref.child("Users").child(Uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    accountData = snapshot.getValue(AccountData.class);
                    Position = accountData.getPosition();

                    Log.d(TAG, "onDataChange: "+Position);
                    if (Position.equals("user")){
                        area_discrete_seek_bar.setVisibility(View.VISIBLE);
                        int Distance = snapshot.child("distance").getValue(Integer.class);
                        discreteSeekBar.setProgress(Distance);
                    }else{
                        area_discrete_seek_bar.setVisibility(View.GONE);
                    }
                    setInfoUser(accountData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setInfoUser(AccountData accountData){
        String Address = (accountData.getAddress() == "") ? "No address." : accountData.getAddress();

        name.setText(accountData.getName());
        address.setText(Address);
        email.setText(accountData.getEmail());
        phone.setText(accountData.getPhone());

        try {
            Picasso.get().load(accountData.getProfile_Url()).into(profile);
        }catch (Exception e){
            profile.setImageResource(R.drawable.ic_person_gray);
        }
    }

    private void updateChildProfile_Url(UploadTask.TaskSnapshot taskSnapshot){
        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
        while (!uriTask.isSuccessful());

        Uri downloadUri = uriTask.getResult();
        if(uriTask.isSuccessful()){
            ref.child("Users").child(Uid).child("profile_Url").setValue(downloadUri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Upload image successfully.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public void UploadImage(byte[] final_image){
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StorageReference imagePath = storageReference.child(Position).child(Uid).child("profile").child("profile.jpg");
        UploadTask uploadTask = imagePath.putBytes(final_image);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateChildProfile_Url(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    public interface onFragmentBtnSelect{
        public void logout();
        public void onButtonSelect();
    }

}

//      Storage     //
//    private boolean checkStoragePermission(){
//        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }
//
//    private void requestStoragePermission(){
//        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
//    }

//      Camera     //
//    private boolean checkCameraPermission(){
//        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
//        boolean result2 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result1 && result2;
//    }
//
//    private void requestCameraPermission(){
//        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case CAMERA_REQUEST_CODE:{
//                if(grantResults.length > 0){
//                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if(cameraAccepted && writeStorageAccepted){
//                        pickFromCamera();
//                    }else{
//                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//            case STORAGE_REQUEST_CODE:{
//                if(grantResults.length > 0){
//                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if(writeStorageAccepted){
//                        pickFromGallery();
//                    }else{
//                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//            break;
//        }
//    }
//
//    private void pickFromCamera() {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.TITLE,"Temp Pic");
//        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
//
//        image_Uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_Uri);
//        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
//    }
//
//    private void pickFromGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == IMAGE_PICK_CAMERA_CODE && resultCode == getActivity().RESULT_OK){
//            profile.setImageURI(image_Uri);
//        }else{
//            image_Uri = data.getData();
//            profile.setImageURI(image_Uri);
//        }
//    }
//
//    private void showImagePickDialog() {
//        String []option = {"Camera","Gallery"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Pick image form");
//        builder.setCancelable(true);
//        builder.setItems(option, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(which == 0){
//                    if(!checkCameraPermission())
//                        requestCameraPermission();
//                    else
//                        pickFromCamera();
//                }else{
//                    if(!checkStoragePermission())
//                        requestStoragePermission();
//                    else
//                        pickFromGallery();
//                }
//            }
//        });
//        builder.create().show();
//    }