package com.example.carlosjof.prjreport.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carlosjof.prjreport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText textViewUser;
    private EditText textViewPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewUser = findViewById(R.id.txtUser);
        textViewPassword = findViewById(R.id.txtPassword);

        findViewById(R.id.btnlogin).setOnClickListener(this);
        findViewById(R.id.btnsingup).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(LogIn.this, ShowPost.class);
            startActivity(intent);
            finish();
        }
    }

    private void logIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        progressDialog.setMessage("Iniciando Sesion...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LogIn.this, "Bienvenido " + textViewUser.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LogIn.this, ShowPost.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LogIn.this, "Error al Iniciar Sesion", Toast.LENGTH_SHORT).show();
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


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnlogin:
                logIn(textViewUser.getText().toString(), textViewPassword.getText().toString());
                break;
            case R.id.btnsingup:
                Intent intent = new Intent(this, SignIn.class);
                startActivity(intent);
                break;
        }
    }
}
