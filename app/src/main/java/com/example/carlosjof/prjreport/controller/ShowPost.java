package com.example.carlosjof.prjreport.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.carlosjof.prjreport.R;
import com.example.carlosjof.prjreport.model.UploadImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowPost extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ImageButton imageButton;
    private Button button;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    private List<UploadImage> uploadImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_listreports);

        recyclerView = findViewById(R.id.recycle_activity_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btn_activity_post_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowPost.this);
                    builder.setMessage("¿Deseas cerrar sesión?")
                            .setTitle("¡Hola " + user.getEmail() + "!")
                            .setCancelable(false)
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAuth = FirebaseAuth.getInstance();

                                    mAuth.signOut();
                                    updateUI(null);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        imageButton = findViewById(R.id.btnupload_activity_post);
        findViewById(R.id.btnupload_activity_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowPost.this, UploadContent.class);
                startActivity(intent);
                finish();
            }
        });

        uploadImages = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("upload");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    UploadImage uploadImage = postSnapShot.getValue(UploadImage.class);
                    uploadImages.add(uploadImage);
                    Collections.reverse(uploadImages);
                }
                imageAdapter = new ImageAdapter(ShowPost.this, uploadImages);
                recyclerView.setAdapter(imageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ShowPost.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Intent intent = new Intent(ShowPost.this, LogIn.class);
            startActivity(intent);
            finish();
        }
    }


}
