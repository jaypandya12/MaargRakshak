package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.myapplication.R;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragments.HomeFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RegisterComplaint extends AppCompatActivity {
    int PERMISSION_ID = 44;

    String eventname, eventdesc, eventimage;
    ImageView imageView;
    FirebaseAuth mAuth;
    private static final int CHOOSE_IMAGE = 101;
    String eventImageUrl;
    Uri uriEventImage;
    ProgressBar progressBar;
    EditText eventName;
    EditText eventVenue;
    EditText eventDesc;
    EditText eventTime;
    Button eventButton;
    TextView like, like_count;
    StorageReference storageReference;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("phpdemo");
    static int eventcount = 0;
    int initlikes = 0;

    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint);
        mAuth = FirebaseAuth.getInstance();
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        eventName = (EditText) findViewById(R.id.addEventName);
        eventVenue = (EditText) findViewById(R.id.addEventVenue);
        eventDesc = (EditText) findViewById(R.id.addEventDesc);
        eventTime = (EditText) findViewById(R.id.addEventTime);
        eventButton = (Button) findViewById(R.id.buttonAddEvent);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        loadUserInformation();

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEventToDB(eventcount);
                finish();
                startActivity(new Intent(RegisterComplaint.this, MainActivity.class));
            }
        });
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                CHOOSE_IMAGE);
    }

private void uploadImageToFirebaseStorage() {
//        {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("PotholePic/" + System.currentTimeMillis() + ".jpg");

        if (uriEventImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriEventImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // When the image has successfully uploaded, we get its download URL
                        progressBar.setVisibility(View.GONE);
                        eventImageUrl = task.getResult().toString();

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterComplaint.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriEventImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriEventImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                eventimage = user.getPhotoUrl().toString();
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
        }
    }

    private void addEventToDB(int eventcount) {
        if (eventName.getText().toString().isEmpty()) {
            eventName.setError("Name required");
            eventName.requestFocus();
            return;
        }
        if (eventVenue.getText().toString().isEmpty()) {
            eventVenue.setError("Venue required");
            eventVenue.requestFocus();
            return;
        }
        if (eventDesc.getText().toString().isEmpty()) {
            eventDesc.setError("Description required");
            eventDesc.requestFocus();
            return;
        }
        if (eventTime.getText().toString().isEmpty()) {
            eventTime.setError("Time required");
            eventTime.requestFocus();
            return;
        }

        eventName = (EditText) findViewById(R.id.addEventName);
        String name = eventName.getText().toString();
        DatabaseReference event1 = rootRef.child(name);
        eventname = name;
        eventVenue = (EditText) findViewById(R.id.addEventVenue);
        String venue = eventVenue.getText().toString();
        eventDesc = (EditText) findViewById(R.id.addEventDesc);
        String desc = eventDesc.getText().toString();
        eventdesc = desc;
        eventTime = (EditText) findViewById(R.id.addEventTime);
        String time = eventTime.getText().toString();
        String lat = latTextView.getText().toString();
        String lon = lonTextView.getText().toString();
        DatabaseReference event_name = event1.child("Event Name: ");
        event_name.setValue(name);
        DatabaseReference event_venue = event1.child("Event Venue: ");
        event_venue.setValue(venue);
        DatabaseReference event_desc = event1.child("Event Description: ");
        event_desc.setValue(desc);
        DatabaseReference event_time = event1.child("Event Time: ");
        event_time.setValue(time);
        DatabaseReference event_lat = event1.child("Event Latitude: ");
        event_lat.setValue(lat);
        DatabaseReference event_lon = event1.child("Event Longitude: ");
        event_lon.setValue(lon);
        DatabaseReference likes_count = event1.child("Likes: ");
        likes_count.setValue("0");
        String image = eventImageUrl;
        DatabaseReference event_image = event1.child("Image: ");
        event_image.setValue(image);
        //
        eventcount++;

    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

}


