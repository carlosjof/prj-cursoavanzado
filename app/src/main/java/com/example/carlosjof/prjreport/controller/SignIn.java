package com.example.carlosjof.prjreport.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carlosjof.prjreport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText textViewUser;
    private EditText textViewPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        textViewUser = findViewById(R.id.txtUser);
        textViewPassword = findViewById(R.id.txtPassword);

        findViewById(R.id.btnbacktologin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnsingin).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null){
            Intent intent = new Intent(SignIn.this, ShowPost.class);
            startActivity(intent);
            finish();
        }
    }

    public void signIn(String email, String password){
        if (!validateForm()){
            return;
        }

        progressDialog.setMessage("Registrado Usuario...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignIn.this, "Bienvenido " + textViewUser.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, ShowPost.class);
                    startActivity(intent);
                    finish();
                }else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(SignIn.this,"Ya este usuario esta registrado", Toast.LENGTH_LONG).show();
                    }else{
                    Toast.makeText(SignIn.this,"Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = textViewUser.getText().toString();

        if (TextUtils.isEmpty(email)) {
            textViewUser.setError("Ingrese Email");
            valid = false;
        } else {
            textViewUser.setError(null);
        }

        String password = textViewPassword.getText().toString();

        if (TextUtils.isEmpty(password)) {
            textViewPassword.setError("Ingrese Contraseña");
            valid = false;
        } else {
            textViewPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnsingin)
            signIn(textViewUser.getText().toString(), textViewPassword.getText().toString());
    }
}
