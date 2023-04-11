package com.example.smarttaskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarttaskmanager.Adapter.NotificationAdapter;
import com.example.smarttaskmanager.databinding.FragmentNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class NotificationFragment extends Fragment {

    final private Context context = getContext();
    private FragmentNotificationBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private NotificationAdapter adapter;
    private ArrayList<NotificationData> notifications;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();

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
        binding = FragmentNotificationBinding.inflate(inflater,container,false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notifications);
        binding.recyclerView2.setAdapter(adapter);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(context));

        binding.notificationProgress.setVisibility(View.VISIBLE);

        database.getReference().child("Notifications/" + auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    notifications.add(snapshot1.getValue(NotificationData.class));
                }
                binding.notificationProgress.setVisibility(View.GONE);
                if (notifications.isEmpty()) {
                    binding.notificationEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    binding.notificationEmptyMessage.setVisibility(View.GONE);

                }
                Collections.reverse(notifications);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}