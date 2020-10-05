package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btn_back;
    private CircularImageView image_profile;
    private HashMap<String,Object> hashMap;

    private String cameraPermissions[];

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CAMERA_CODE = 200;

    private Uri image_Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    ///////////* permissions ที่ต้องใช้สำหรับกล้องได้แก่ 1.เรียก camera 2.บันทึกภาพถ่ายลงไปใน Ex-Storage *///////////////////
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /////////////////////////*      getIntent เพื่อรับ intent       *////////////////////////////////////
        Intent getIntent = getIntent();

    /////////*    check intent box ว่ามามี intent box "RMA" ไหม แล้ว hashMap ที่ได้จะเป็นข้อมูลของฝั่ง maid *///////////
        if(getIntent.hasExtra("ICA"))
            hashMap = (HashMap<String,Object>)(getIntent.getSerializableExtra("ICA"));
        else {
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    ///////////////////////////////////*  อ้างอิงปุ่มและดัก Action เพื่อย้อนกลับ  *//////////////////////////////////
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    ////////////////////////////*  อ้างอิง Image และดัก Action เพื่อ Upload image  *///////////////////////////
        image_profile = findViewById(R.id.image_profile);
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCameraPermission())
                    requestCameraPermission();
                else
                    pickFromCamera();
            }
        });
    }

    ///////////////////////////*    Request Permission Camera */////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    }

    /* ถ่ายรูปแล้วกด ok ก็จะทำการเปิดหน้า crop image แล้วจะได้รูปที่ crop ออกมา แล้ว compressor image จากนั้น call setAndAddImage function */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICK_CAMERA_CODE && resultCode == RESULT_OK){// && data != null งงว่าทำไมโทรศัพท์จริงๆใช้ไม่ได้ ?
            CropImage.activity(image_Uri).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(ProfileActivity.this);

            Toast.makeText(this, "Camera: "+image_Uri, Toast.LENGTH_LONG).show();
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

                setAndAddImage(final_image);

                Toast.makeText(this, "Image: "+image_Uri, Toast.LENGTH_LONG).show();

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, ""+result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////////////////////////////*      Camera (Check)     *////////////////////////////////////
    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }

    ////////////////////////////////*      Camera (Request)     *///////////////////////////////////
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
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

    /////////////////////*  set image ที่ image view แล้วทำการ put(crop image) ลงใน hashMap *///////////////
    private void setAndAddImage(byte[] final_image) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(final_image, 0, final_image.length);
        image_profile.setImageBitmap(bitmap);
        hashMap.put("Profile_Uri",final_image);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ProfileActivity.this,ChooseGenderActivity.class);
                intent.putExtra("PFA",hashMap);
                startActivity(intent);
            }
        },2000);
    }
}

/* Comments 1/10/2020 19:00 Check:True
    - กรณีที่ใช้งาน app ครั้งแรก ระบบจะทำการ checkPermission
        - ถ้ายังไม่ได้อนุญาติ จะทำการ Request permission
        - ถ้าอนุญาติแล้ว จะทำการ เข้าถึง Camera
    - เมื่อถ่ายรูปเสร็จแล้วกด ok จะทำการเปิด Crop image activity เพื่อ Crop ส่วนที่ใช้งานแล้วนำรูปที่ Crop ไป Compressor
    - เมื่อ Compressor จะได้รูปออกมา แล้วนำไป set image กับ put ลงไปใน hashMap แล้วส่งไปยัง ChooseGenderActivity ด้วย Intent box "PFA"
*/