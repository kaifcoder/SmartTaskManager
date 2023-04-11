package com.example.smarttaskmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.smarttaskmanager.Adapter.TaskAdapter;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    final String[] taskCategories = {"ALL Tasks", "TO-DO", "IN PROGRESS", "DONE"};
    final int ALL = 0, TODO = 1, IN_PROGRESS = 2, DONE = 3;
    private ArrayAdapter taskCategoryAdapter;
    private TaskAdapter taskAdapter;
    private String currentUserId;
    private ArrayList<Task> userTasks;
    private Users user;

    private FirebaseDatabase database;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(Users user) {
        this.user = user;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        userTasks = new ArrayList<>();

        binding.homeProgress.setVisibility(View.VISIBLE);

        if (user != null) {
            binding.selectedUserView.setVisibility(View.VISIBLE);
            binding.selectedUserName.setText(user.getUserName());
            taskAdapter = new TaskAdapter(getContext(), userTasks, "");
            currentUserId = user.getFireuserid();
            binding.selectedUserName.setText(user.getUserName());
            binding.selectedUserMail.setText(user.getUserEmail());

        } else {
            taskAdapter = new TaskAdapter(getContext(), userTasks, null);
            currentUserId = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users/"+currentUserId+"/").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    binding.selectedUserName.setText(user.getUserName());
                    binding.selectedUserMail.setText(user.getUserEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(token -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("token",token);
                    FirebaseDatabase.getInstance().getReference().child("Users/"+currentUserId+"/")
                            .updateChildren(map);
                });

        }

        setValues();
        loadTask();
        binding.ivlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        return binding.getRoot();
    }

    void loadTask() {
        FirebaseDatabase.getInstance().getReference().child("all-tasks")
                .child("user-tasks")
                .child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int todo = 0;
                        int inProgress = 0;
                        int done = 0;
                        int all;
                        userTasks.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            assert task != null;
                            if (task.getTaskStatus().equals(Task.TODO)) {
                                todo++;
                            } else if (task.getTaskStatus().equals(Task.IN_PROGRESS)) {
                                inProgress++;
                            } else {
                                done++;
                            }
                            userTasks.add(task);
                        }
                        Collections.reverse(userTasks);


                        try {
                            binding.todoCount.setText(String.valueOf(todo));
                            binding.inProgressCount.setText(String.valueOf(inProgress));
                            binding.doneCount.setText(String.valueOf(done));
                        } catch (Exception ignored) {

                        }

                        taskAdapter.notifyDataSetChanged();

                        try {

                            binding.homeProgress.setVisibility(View.GONE);

                            if (userTasks.isEmpty()) {
                                binding.taskEmptyMsg.setText("No tasks available");
                                binding.taskEmptyMsg.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                binding.taskEmptyMsg.setVisibility(View.GONE);

                            }
                        } catch (Exception ignored) {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void setValues() {
        taskCategoryAdapter = new ArrayAdapter(getContext(), R.layout.home_list_item, taskCategories);
        binding.taskCategory.setAdapter(taskCategoryAdapter);

        binding.taskCategory.setOnItemClickListener(taskCategoryListener);

        binding.userTaskRecylerView.setAdapter(taskAdapter);
        binding.userTaskRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    AdapterView.OnItemClickListener taskCategoryListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == ALL) {
                taskAdapter.applyFilter(userTasks);
            }
            else if (position == TODO) {
                ArrayList<Task> todoTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.TODO)) {
                        todoTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(todoTasks);
            }
            else if (position == IN_PROGRESS) {
                ArrayList<Task> inProgressTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.IN_PROGRESS)) {
                        inProgressTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(inProgressTasks);
            }
            else {
                ArrayList<Task> doneTasks = new ArrayList<>();
                for (Task task : userTasks) {
                    if (task.getTaskStatus().equals(Task.DONE)) {
                        doneTasks.add(task);
                    }
                }
                taskAdapter.applyFilter(doneTasks);
            }
        }
    };
}