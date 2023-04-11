package com.example.smarttaskmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.smarttaskmanager.Adapter.UserAdapter;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.databinding.ActivitySubmitTaskBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitTaskActivity extends AppCompatActivity {
    final Context context = SubmitTaskActivity.this;
    private ActivitySubmitTaskBinding binding;
    private Task selectedTask;
    private UserAdapter adapter;
    private FirebaseDatabase database;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();

        selectedTask = (Task) getIntent().getSerializableExtra("selectedTask");
        adapter = new UserAdapter(context, selectedTask.getGrpTask(), null);
        setValues();

        binding.updateTask.setOnClickListener(
                updateTaskButton
        );
        binding.backButton.setOnClickListener(v -> finish());
    }
    View.OnClickListener updateTaskButton = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if (binding.statusGroup.getCheckedChipId() == -1) {
                Toast.makeText(context, "Please select priority", Toast.LENGTH_SHORT).show();
                return;
            }
            String path = "all-tasks/user-tasks";
            selectedTask.setTaskStatus(getSelectedStatus());
            selectedTask.setTaskDescription(binding.taskDescSubmit.getText().toString().trim());

            for (Users user : selectedTask.getGrpTask()) {
                database.getReference().child(path).child(user.getFireuserid())
                        .child(selectedTask.getTaskID())
                        .setValue(selectedTask)
                        .addOnCompleteListener(task -> {
                            if (count == selectedTask.getGrpTask().size() - 1) {
                                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                                CommonUtils.sendNotificationToUser(selectedTask,context,selectedTask.getTaskTitle(),"Task Updated: "+selectedTask.getTaskStatus() +"\nTask Description: "+selectedTask.getTaskDescription());
                                count = 0;
                            } else {
                                count++;
                            }
                        });

            }
        }
    };
    private void setValues() {
        binding.taskTitleSubmit.setText(selectedTask.getTaskTitle());
        binding.taskDescSubmit.setText(selectedTask.getTaskDescription());
        binding.participants.setAdapter(adapter);
        binding.participants.setLayoutManager(new GridLayoutManager(context, 2));
        binding.startDate.setText(selectedTask.getTaskAssigned());
        binding.dueDate.setText(selectedTask.getTaskDeadline());
        setTaskStatus();
    }

    private void setTaskStatus() {
        String status = selectedTask.getTaskStatus();
        if (status.equals(Task.TODO)) {
            binding.Todo.setChecked(true);
        }
        if (status.equals(Task.DONE)) {
            binding.Done.setChecked(true);
        }
        if (status.equals(Task.IN_PROGRESS)) {
            binding.inProgress.setChecked(true);
        }
    }

    private String getSelectedStatus() {
        if (binding.Todo.isChecked()) {
            return Task.TODO;
        } else if (binding.inProgress.isChecked()) {
            return Task.IN_PROGRESS;
        } else {
            return Task.DONE;
        }
    }
}