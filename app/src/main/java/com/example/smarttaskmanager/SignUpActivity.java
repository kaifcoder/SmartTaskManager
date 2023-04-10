package com.example.smarttaskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    final String[] deptCategory = {Users.Android_dept, Users.Web_Dept, Users.Ui_Ux_Dept, Users.MBA_Dept};
    final int Android_Dev = 0, Web_Dev = 1, UI_UX = 2, MBA = 3;
    private ArrayAdapter fieldCategoryAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firebase declarations
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        user = new Users();
//        load field
        fieldCategoryAdapter = new ArrayAdapter(getApplicationContext(), R.layout.home_list_item, deptCategory);
        binding.createUserField.setAdapter(fieldCategoryAdapter);
        binding.createUserField.setOnItemClickListener((parent, view, position, id) -> user.setUserDept(deptCategory[position]));

        binding.createUserLoginButton.setOnClickListener(v -> {

            String email = binding.createUserEmail.getText().toString().trim();
            String password = binding.createUserPass.getText().toString().trim();
            String username = binding.crateUserName.getText().toString().trim();


            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Fill All Fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 7) {

                Toast.makeText(getApplicationContext(), "Must be 8 characters or more", Toast.LENGTH_SHORT).show();

            } else if (binding.createUserField.getText().toString().equals("Select Field")) {
                Toast.makeText(getApplicationContext(), "Please select a field", Toast.LENGTH_SHORT).show();
            } else {
                user.setUserEmail(binding.createUserEmail.getText().toString().trim());
                user.setUserPass(binding.createUserPass.getText().toString().trim());
                user.setUserName(binding.crateUserName.getText().toString().trim());
                user.setUserProfile(Users.No_Profile);

                //register user
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Resgistration Succesfull", Toast.LENGTH_SHORT).show();
                        sendEmailVerification();
                        user.setFireuserid(firebaseAuth.getUid());
                        updateUser(user);
//                                resetAttributes();


                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            }

        });
        
    }

    private void updateUser(Users username) {
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).setValue(username).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "user is in database", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                Log.d("result", "onComplete: " + task.getException());
            } else {
                Toast.makeText(getApplicationContext(), "user is not add in database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                Toast.makeText(getApplicationContext(), "Verification e-mail Sent", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to send verification e-mail", Toast.LENGTH_SHORT).show();
        }
    }
}