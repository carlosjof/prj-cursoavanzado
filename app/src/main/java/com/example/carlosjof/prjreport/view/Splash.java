package com.example.carlosjof.prjreport.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.carlosjof.prjreport.controller.LogIn;
import com.example.carlosjof.prjreport.controller.ShowPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(Splash.this, ShowPost.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(Splash.this, LogIn.class);
            startActivity(intent);
            finish();
        }
    }
}
