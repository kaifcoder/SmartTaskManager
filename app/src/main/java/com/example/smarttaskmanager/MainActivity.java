package com.example.smarttaskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smarttaskmanager.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Validation login details please wait!");

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        binding.signupbutton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));

        });

        binding.loginbutton.setOnClickListener(v -> {
            String mail = binding.loginEmail.getText().toString().trim();
            String password = binding.loginPassword.getText().toString().trim();
            if(mail.isEmpty()||password.isEmpty()){
                Toast.makeText(this, "all fields are required to fill", Toast.LENGTH_SHORT).show();
            } else if (password.length()<7) {
                Toast.makeText(this, "password must be 8 characters", Toast.LENGTH_SHORT).show();
            } else{
                dialog.show();
                firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        checkMailVerification();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private void checkMailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true){
            Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            dialog.dismiss();
            finish();
        } else {
            dialog.dismiss();
            Toast.makeText(this, "User Not verified", Toast.LENGTH_SHORT).show();
        }
    }
}