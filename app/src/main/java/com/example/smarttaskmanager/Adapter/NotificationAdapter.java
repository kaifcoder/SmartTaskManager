package com.example.smarttaskmanager.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttaskmanager.CommonUtils;
import com.example.smarttaskmanager.NotificationData;
import com.example.smarttaskmanager.R;
import com.example.smarttaskmanager.databinding.NotificationLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final Context context;
    private final ArrayList<NotificationData> notificationData;

    public NotificationAdapter(Context context, ArrayList<NotificationData> notificationData) {
        this.context = context;
        this.notificationData = notificationData;

    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        String title = notificationData.get(holder.getAdapterPosition()).getNotificationTitle();
        String msg = notificationData.get(holder.getAdapterPosition()).getNotificationMessage();
        String timeAndDate = notificationData.get(holder.getAdapterPosition()).getNotificationDate();
        String id = notificationData.get(holder.getAdapterPosition()).getNotificationId();

        holder.binding.notificationTitle.setText(title);
        holder.binding.notificationDesc.setText(msg);
        holder.binding.notificationDate.setText(timeAndDate);

        holder.binding.taskItem.setOnLongClickListener(v -> {
            AlertDialog dialog = CommonUtils.generateAlertDialog(context, "Do you want to delete this notification?",
                    (dialog1, which) -> {
                        String path = "Notifications/" + FirebaseAuth.getInstance().getUid() + "/" + id;
                        FirebaseDatabase.getInstance().getReference(path).removeValue().addOnSuccessListener(unused -> {
                            CommonUtils.showToast(context, "Notification Deleted");
                        });
                    }, (dialog12, which) -> dialog12.dismiss());
            dialog.show();
            return false;
        });


        holder.binding.taskItem.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(msg)
                    .show();
        });

    }

    public void reset() {
    }


    @Override
    public int getItemCount() {
        return notificationData.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private NotificationLayoutBinding binding;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = binding.bind(itemView);
        }
    }
}
