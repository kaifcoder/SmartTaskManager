package com.example.smarttaskmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.example.smarttaskmanager.Adapter.PasswordAdapter;
import com.example.smarttaskmanager.Models.Passwords;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.databinding.DialogAddpasswordBinding;
import com.example.smarttaskmanager.databinding.DialogUpdatepasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smarttaskmanager.databinding.ActivitySecureVaultBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecureVault extends AppCompatActivity {

    private ActivitySecureVaultBinding binding;
    private DialogAddpasswordBinding dialogAddpasswordBinding;
    private Dialog dialog;
    private DialogUpdatepasswordBinding dialogUpdatepasswordBinding;

    private List<Passwords> passwordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySecureVaultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        passwordsList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user-passwords/"+ FirebaseAuth.getInstance().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                passwordsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Passwords passwords = snapshot.getValue(Passwords.class);
                    passwordsList.add(passwords);
                }
                binding.passwordList.setAdapter(new PasswordAdapter(SecureVault.this,passwordsList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SecureVault.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        binding.passwordList.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SecureVault.this);
            builder.setTitle("Password Details")
                    .setMessage("ID: " + passwordsList.get(position).getAccid() + "\nPassword: " + passwordsList.get(position).getPassword())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("Edit Password", (dialog1, which) -> {
                        dialogUpdatepasswordBinding = DialogUpdatepasswordBinding.inflate(getLayoutInflater());
                        dialog = new Dialog(SecureVault.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(dialogUpdatepasswordBinding.getRoot());
                        dialogUpdatepasswordBinding.addpasswordbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newPass = dialogUpdatepasswordBinding.passwordedt.getText().toString().trim();
                                Map<String, Object> map = new HashMap<>();
                                map.put("password", newPass);
                                FirebaseDatabase.getInstance().getReference()
                                        .child("user-passwords/" + FirebaseAuth.getInstance().getUid() + "/" + passwordsList.get(position).getId())
                                        .updateChildren(map, (error, ref) -> {
                                            if (error != null) {
                                                // Handle error
                                            } else {
                                                // Password updated successfully
                                                Toast.makeText(SecureVault.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        });

                        ImageView cancelButton = dialogUpdatepasswordBinding.backButton;
                        cancelButton.setOnClickListener(view1 -> dialog.dismiss());
                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog1.dismiss();
                    })
                    .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog2, int which) {
                            showDeleteDialog(position);
                            dialog2.dismiss();
                        }
                    });

            AlertDialog dialog1 = builder.create();
            dialog1.show();
        });



        binding.fab.setOnClickListener(view -> {
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            showBottomDialog();
        });
    }

    private void showDeleteDialog(int position) {
        AlertDialog dialog1234 = CommonUtils.generateAlertDialog(SecureVault.this, "Do you want to delete this notification?",
                (dialog13, which) -> {
                    String path = "user-passwords/" + FirebaseAuth.getInstance().getUid() + "/" + passwordsList.get(position).getId();
//                    Toast.makeText(SecureVault.this, path, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference(path).removeValue().addOnSuccessListener(unused -> {
                        CommonUtils.showToast(SecureVault.this, "Password Deleted");
                    });
                }, (dialog14, which) -> dialog14.dismiss());
        dialog1234.show();
    }

    private void showBottomDialog() {
        dialogAddpasswordBinding = DialogAddpasswordBinding.inflate(getLayoutInflater());
        dialog = new Dialog(SecureVault.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogAddpasswordBinding.getRoot());

        dialogAddpasswordBinding.addpasswordbtn.setOnClickListener(v -> {
            if(dialogAddpasswordBinding.accid.getText().toString().isEmpty()||dialogAddpasswordBinding.passwordedt.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
            }
            Passwords passwords = new Passwords();
            passwords.setId(CommonUtils.generateId());
            passwords.setPassword(dialogAddpasswordBinding.passwordedt.getText().toString().trim());
            passwords.setAccid(dialogAddpasswordBinding.accid.getText().toString().trim());
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you really want to Add this Password");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int id) {
                            addTaskToDatabase();
                        }

                        synchronized private void addTaskToDatabase() {
                                String path = "user-passwords/"+ FirebaseAuth.getInstance().getUid()+"/"+passwords.getId();
                                FirebaseDatabase.getInstance().getReference().child(path).setValue(passwords).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    synchronized public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Password Saved", Toast.LENGTH_SHORT).show();
                                            resetAllInputs();
                                            dialog.dismiss();
                                    }
                                });

                        }

                        private void resetAllInputs() {
                            dialogAddpasswordBinding.passwordedt.setText("");
                            dialogAddpasswordBinding.accid.setText("");
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());
            AlertDialog alert11 = builder1.create();
            alert11.show();

        });

        ImageView cancelButton = dialogAddpasswordBinding.backButton;
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


}