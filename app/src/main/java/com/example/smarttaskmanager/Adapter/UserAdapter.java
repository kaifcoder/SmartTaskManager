package com.example.smarttaskmanager.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttaskmanager.HomeActivity;
import com.example.smarttaskmanager.Models.Users;
import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.databinding.UserItemBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {
    private final Context context;
    private final ArrayList<Users> users;
    private final String from;

    public UserAdapter(Context context, ArrayList<Users> users, String from) {
        this.context = context;
        this.users = users;
        this.from = from;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UsersViewHolder holder, int position) {
        holder.binding.userName.setText(users.get(position).getUserName());
        holder.binding.clearTextBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Doing this will remove this user from the task, are you sure?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> removeItem(holder.getAdapterPosition()));
            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });
    }
    public void removeItem(int poi) {
        if (from != null && !from.equals("SendNotification")) {
            FirebaseDatabase.getInstance().getReference().child("all-tasks/user-tasks").child(users.get(poi).getFireuserid()).child(from).setValue(null);
        }

        if (from != null && from.equals("SendNotification")) {
            HomeActivity.showingItems.add(users.get(poi).getUserName());
            HomeActivity.items.add(users.remove(poi));
        } else {
            try {
                HomeActivity.showingItems.add(users.get(poi).getUserName());
                HomeActivity.items.add(users.remove(poi));
            } catch (Exception e) {

            }

        }


        notifyItemRemoved(poi);
        notifyItemRangeChanged(poi, users.size());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        UserItemBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = binding.bind(itemView);
        }
    }
}
