package com.example.smarttaskmanager.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttaskmanager.CommonUtils;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.SubmitTaskActivity;
import com.example.smarttaskmanager.databinding.TaskLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final Context context;
    private ArrayList<Task> tasks;
    private final String from;

    public TaskAdapter(Context context, ArrayList<Task> tasks, String from) {
        this.context = context;
        this.tasks = tasks;
        this.from = from;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        String priority = tasks.get(position).getTaskPriority();
        if (priority.equals(Task.LOW)) {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_green));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_green)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        } else if (priority.equals(Task.MEDIUM)) {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_yellow));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_yellow));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_yellow));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_yellow)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        } else {
            holder.binding.taskItem.setCardBackgroundColor(context.getResources().getColor(R.color.low_red));
            holder.binding.startingDate.setTextColor(context.getResources().getColor(R.color.dark_red));
            holder.binding.deadlineDate.setTextColor(context.getResources().getColor(R.color.dark_red));
            holder.binding.priorityShow.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.dark_red)));
            holder.binding.priorityShow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
            holder.binding.userTaskTitle.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.title_text)));
            holder.binding.userTaskDescription.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.desc_text)));
        }


        holder.binding.userTaskTitle.setText(tasks.get(position).getTaskTitle());
        holder.binding.userTaskDescription.setText(tasks.get(position).getTaskDescription());
        holder.binding.startingDate.setText(tasks.get(position).getTaskAssigned());
        holder.binding.deadlineDate.setText(tasks.get(position).getTaskDeadline());
        holder.binding.priorityShow.setText(tasks.get(position).getTaskPriority());


        if (from == null) {
            holder.binding.taskItem.setOnClickListener(v -> {
                Intent intent = new Intent(context, SubmitTaskActivity.class);
                intent.putExtra("selectedTask", tasks.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            });
            holder.binding.taskItem.setOnLongClickListener(v -> {
                AlertDialog dialog = CommonUtils.generateAlertDialog(context, "Do you want to delete this Task?",
                        (dialog1, which) -> {
                            final Users[] user = {new Users()};
                            String path = "all-tasks/user-tasks/" + FirebaseAuth.getInstance().getUid()+"/" + tasks.get(holder.getAdapterPosition()).getTaskID();
                            FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    user[0] = snapshot.getValue(Users.class);
                                    CommonUtils.sendNotificationToUser(tasks.get(holder.getAdapterPosition()),context,"Task deleted by "+ user[0].getUserName(),"Deleted task "+tasks.get(holder.getAdapterPosition()).getTaskTitle());
                                    FirebaseDatabase.getInstance().getReference().child(path).setValue(null).addOnSuccessListener(unused -> {
                                        CommonUtils.showToast(context, "Task deleted successfully");
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        }, (dialog12, which) -> dialog12.dismiss());
                dialog.show();
                return false;
            });
        }


    }


    public void applyFilter(ArrayList<Task> filteredTask) {
        tasks = filteredTask;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TaskLayoutBinding binding;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = binding.bind(itemView);
        }
    }
}