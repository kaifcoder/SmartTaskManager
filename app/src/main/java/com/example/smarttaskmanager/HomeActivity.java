package com.example.smarttaskmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.example.smarttaskmanager.Adapter.UserAdapter;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.databinding.ActivityHomeBinding;
import com.example.smarttaskmanager.databinding.DialogMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    DialogMainBinding dialogMainBinding;
    Dialog dialog;
    private Task selectedTask;
    private boolean isTaskSubmitted = false;
    private MaterialDatePicker startDatePicker;
    private MaterialDatePicker dueDatePicker;
    private ListPopupWindow userList;
    private UserAdapter adapter;
    private ArrayAdapter userListAdapter;
    private ArrayList<Users> assignedList;
    public static ArrayList<Users> items;
    public static ArrayList<String> showingItems;
    public static ArrayList<String> grpTokens;
    private boolean isEdit = false;
    final String USER_TASK_PATH = "all-tasks/user-tasks";
    final String USERS_PATH = "Users";
    final String PROGRESS_MESSAGE = "Assigning Task";
    private FirebaseDatabase database;
    public static int count = 0;
    private ProgressDialog progressDialog;
    private ImageButton cancelButton;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String USER_PATH = "Users/" + auth.getUid() + "/";
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    binding.addTaskBtn.setVisibility(View.VISIBLE);
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_dashboard:
                    binding.addTaskBtn.setVisibility(View.INVISIBLE);
                    replaceFragment(new DashboardFragment());
                    break;
                case R.id.charts:
                    binding.addTaskBtn.setVisibility(View.INVISIBLE);
                    replaceFragment(new ChartFragment());
                    break;
                case R.id.nav_calender:
                    binding.addTaskBtn.setVisibility(View.VISIBLE);
                    replaceFragment(new CalenderFragment());
                    break;
                case R.id.navigation_notifications:
                    binding.addTaskBtn.setVisibility(View.INVISIBLE);
                    replaceFragment(new NotificationFragment());
                    break;
            }
            loadUsers();
            selectedTask = (Task) getIntent().getSerializableExtra("selectedTask");
            return true;
        });

        perm();

        binding.addTaskBtn.setOnClickListener(view -> showBottomDialog());


        database.getReference().child(USER_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void perm() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            int permissionNotif = ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS);
            if(permissionNotif!= PackageManager.PERMISSION_GRANTED){
                String[] NOTIF_PERM = {
                        Manifest.permission.POST_NOTIFICATIONS
                };
                ActivityCompat.requestPermissions(this,NOTIF_PERM,1);
            }
        }
    }



    private void loadUsers() {
        database.getReference().child(USERS_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    items.clear();
                    showingItems.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Users user = snap.getValue(Users.class);
                        boolean add = true;
                        for (Users users : assignedList) {
                            if (users.getFireuserid().equals(user.getFireuserid())) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            items.add(user);
                            showingItems.add(user.getUserName());
                        }

                    }

                    userListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("TAG", "onDataChange: ");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void showBottomDialog() {

        dialogMainBinding = DialogMainBinding.inflate(getLayoutInflater());
        dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogMainBinding.getRoot());
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        startDatePicker = materialDateBuilder.build();
        dueDatePicker = materialDateBuilder.build();
        assignedList = new ArrayList<>();
        items = new ArrayList<>();
        showingItems = new ArrayList<>();
        userList = new ListPopupWindow(this);
        if (isEdit) {
            adapter = new UserAdapter(this, assignedList, selectedTask.getTaskID());
        } else {
            adapter = new UserAdapter(this, assignedList, null);
        }
        //date picker
        View.OnClickListener startDatePick;
        startDatePick = v -> startDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        dialogMainBinding.startDate.setOnClickListener(startDatePick);
        View.OnClickListener dueDatePick;
        dueDatePick = v -> dueDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        dialogMainBinding.dueDate.setOnClickListener(dueDatePick);
        MaterialPickerOnPositiveButtonClickListener dueDateOnPositive;
        dueDateOnPositive = selection -> {
            String dateString = dateFormat.format(new Date((Long) selection));
            materialDateBuilder.setTitleText(dateString);
            dialogMainBinding.dueDate.setText(dateString);
        };
        dueDatePicker.addOnPositiveButtonClickListener(dueDateOnPositive);
        MaterialPickerOnPositiveButtonClickListener startDateOnPositive;
        startDateOnPositive = selection -> {
            String dateString = dateFormat.format(new Date((Long) selection));
            materialDateBuilder.setTitleText(dateString);
            dialogMainBinding.startDate.setText(dateString);
        };
        startDatePicker.addOnPositiveButtonClickListener(startDateOnPositive);


        userListAdapter = new ArrayAdapter(getApplicationContext(), R.layout.user_list,showingItems);
        dialogMainBinding.assignTaskToUserBtn.setOnClickListener(v ->
        {
            loadUsers();
            userList.setHeight(ListPopupWindow.WRAP_CONTENT);
            userList.setWidth(600);
            userList.setAnchorView(v);
            userList.setAdapter(userListAdapter);
            userListAdapter.notifyDataSetChanged();
            userList.setOnItemClickListener((parent, view, position, id) -> {
                assignedList.add(items.get(position));
                items.remove(position);
                showingItems.remove(position);
                userList.dismiss();
                adapter.notifyDataSetChanged();
            });
            userList.show();
        });
        dialogMainBinding.taskAssignTo.setLayoutManager(new GridLayoutManager(this, 2));
        dialogMainBinding.taskAssignTo.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        dialogMainBinding.createTaskbtn.setOnClickListener(v -> {
            if (dialogMainBinding.taskTitle.getText().toString().isEmpty() || dialogMainBinding.taskDescription.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;

            }

            if (dialogMainBinding.priorityGroup.getCheckedChipId() == -1) {
                Toast.makeText(this, "Please select priority", Toast.LENGTH_SHORT).show();
                return;
            }

            if (assignedList.isEmpty()) {
                Toast.makeText(this, "Please Assign task to someone", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dialogMainBinding.startDate.getText().toString().equals("Pick Start Date ....")) {
                Toast.makeText(this, "Please Pick start date", Toast.LENGTH_SHORT).show();
                return;

            }

            if (dialogMainBinding.dueDate.getText().toString().equals("Pick Due Date ....")) {
                Toast.makeText(this, "Please Pick a due date", Toast.LENGTH_SHORT).show();
                return;

            }
            //assign task values
            Task task = new Task();
            if (isEdit) {
                task.setTaskID(selectedTask.getTaskID());
            } else {
                task.setTaskID(CommonUtils.generateId());
            }
            task.setTaskTitle(dialogMainBinding.taskTitle.getText().toString());
            task.setTaskDescription(dialogMainBinding.taskDescription.getText().toString());
            task.setTaskPriority(getSelectedPriority());
            task.setGrpTask(assignedList);
            task.setTaskAssigned(dialogMainBinding.startDate.getText().toString());
            task.setTaskDeadline(dialogMainBinding.dueDate.getText().toString());
            task.setTaskStatus(Task.TODO);

            //add task to database
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you really want to Assign this task");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        public void onClick(DialogInterface dialog, int id) {
                            addTaskToDatabase();
                            CommonUtils.sendNotificationToUser(task, getApplicationContext(),task.getTaskTitle(),task.getTaskDescription());
                            isTaskSubmitted = true;
                        }



                        synchronized private void addTaskToDatabase() {
                            for(Users user: task.getGrpTask()){
                                String path = USER_TASK_PATH+"/"+user.getFireuserid()+"/"+task.getTaskID();
                                database.getReference().child(path).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    synchronized public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                        if (count == assignedList.size() - 1) {
                                            if (!isEdit) {
                                                resetAllInputs();
                                            }
                                            Toast.makeText(getApplicationContext(), "Task Assigned", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {

                                            count++;
                                        }
                                    }
                                });
                            }
                        }

                        private void resetAllInputs() {
                            dialogMainBinding.taskTitle.setText(null);
                            dialogMainBinding.taskDescription.setText(null);
                            dialogMainBinding.highPriority.setChecked(false);
                            dialogMainBinding.mediumPriority.setChecked(false);
                            dialogMainBinding.lowPriority.setChecked(false);
                            assignedList.clear();
                            adapter.notifyDataSetChanged();
                            dialogMainBinding.startDate.setText(null);
                            dialogMainBinding.dueDate.setText(null);
                            dialogMainBinding.taskTitle.requestFocus();

                            loadUsers();
                            count = 0;
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        });

        ImageView cancelButton = dialogMainBinding.backButton;
        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        }

    private String getSelectedPriority() {
        if (dialogMainBinding.highPriority.isChecked()) {
            return "high";
        } else if (dialogMainBinding.mediumPriority.isChecked()) {
            return "medium";
        } else {
            return "low";
        }
    }




    }

