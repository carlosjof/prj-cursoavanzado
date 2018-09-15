package com.example.carlosjof.prjreport.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carlosjof.prjreport.R;
import com.example.carlosjof.prjreport.model.UploadImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class UploadContent extends AppCompatActivity implements View.OnClickListener, LocationListener {


    private TextView textView;
    private EditText editText;
    private EditText editTextLocation;

    private Geocoder geocoder;
    private List<Address> addresses;
    private LocationManager locationManager;
    private String address;

    private ProgressBar progressBar;

    private static final int Image_From_Device = 1;
    private ImageView imageView;
    private Uri UriImage;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        //Buttons
        findViewById(R.id.btn_activity_index_picture).setOnClickListener(this);
        findViewById(R.id.btn_activity_index_uppicture).setOnClickListener(this);

        //EditText Details
        editText = findViewById(R.id.txtDetails);

        //Method GetCurrentLocation
        mylocation();

        imageView = findViewById(R.id.img_activity_index_foto);

        progressBar = findViewById(R.id.progressBar);


        textView = findViewById(R.id.txtback_activity_index);

        //Action go back to de post content
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadContent.this, ShowPost.class);
                startActivity(intent);
                finish();
            }
        });

        //Atuthentication
        mAuth = FirebaseAuth.getInstance();

        //Database & Storage Reference
        storageReference = FirebaseStorage.getInstance().getReference("upload");
        databaseReference = FirebaseDatabase.getInstance().getReference("upload");

        //Permissions to Location
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        //Conditions if user is not login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(UploadContent.this, LogIn.class);
            startActivity(intent);
            finish();
        }
    }

    //Take image from gallery
    private void ImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Image_From_Device);
    }

    //Location listener
    private void mylocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 5000, 5, this);

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    //Show on ImageView the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_From_Device && resultCode == RESULT_OK && data != null && data.getData() != null);

        UriImage = data.getData();

        Picasso.get().load(UriImage).into(imageView);

    }

    //Upload the post on Firebase
    private void uploadImage() {
        if (UriImage != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(UriImage));

            fileReference.putFile(UriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 5000);
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(UploadContent.this, "Se ha subido la imagen", Toast.LENGTH_LONG).show();
                            UploadImage uploadImage = new UploadImage(editText.getText().toString().trim(), uri.toString(), address);
                            String UploadID = databaseReference.push().getKey();
                            databaseReference.child(UploadID).setValue(uploadImage);
                            Intent intent = new Intent(UploadContent.this, ShowPost.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadContent.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });

        } else {
            Toast.makeText(this, "No se hay imagen para publicar", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_activity_index_picture) {
            ImagePicker();
        } else if (i == R.id.btn_activity_index_uppicture) {
            uploadImage();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        editTextLocation = findViewById(R.id.txtLocation);
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
            editTextLocation.setText(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Revise si tiene permisos GPS y conexion a INTERNET", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
