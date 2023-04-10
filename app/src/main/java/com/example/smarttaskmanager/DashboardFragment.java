package com.example.smarttaskmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smarttaskmanager.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    public DashboardFragment() {

    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
        HashMap<String,Object> map = new HashMap<>();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        database.getReference().child("Users/"+auth.getUid()+"/securePass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(getContext(), "secure pass not set", Toast.LENGTH_SHORT).show();
                    binding.unlockbtn.setText("Set Pin");
                    binding.unlockbtn.setOnClickListener(v ->{
                        String securePass = binding.pinView1.getText().toString();
                        map.put("securePass",securePass);
                        FirebaseDatabase.getInstance().getReference().child("Users/"+auth.getUid()+"/")
                                .updateChildren(map);
//                        Toast.makeText(getContext(), securePass, Toast.LENGTH_SHORT).show();
                    });
                }else {
                    String securePassFromDB = snapshot.getValue(String.class);
//                    Toast.makeText(getContext(), "secure pass set", Toast.LENGTH_SHORT).show();
                    binding.tvheadline.setText("Enter your 6 digit Pin");
                    binding.unlockbtn.setText("Unlock");
                    binding.unlockbtn.setOnClickListener(v ->{
                        String securePass = binding.pinView1.getText().toString();
                        if (securePass.equals(securePassFromDB)) {
//                            Toast.makeText(getContext(), "Correct pass", Toast.LENGTH_SHORT).show();
                            // Perform the action you want to perform if the pass is correct
                            Intent intent = new Intent(getActivity(), SecureVault.class);
                            startActivity(intent);
                            binding.pinView1.setText("");
                        } else {
                            Toast.makeText(getContext(), "Incorrect pass", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}