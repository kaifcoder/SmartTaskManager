package com.example.smarttaskmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.Models.Users;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class CommonUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    static public void sendNotificationToUser(Task task, Context context,String title, String message) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (Users user : task.getGrpTask()) {
            String id = generateId();
            String path = "Notifications/"+user.getFireuserid()+"/"+id;
            NotificationData data = new NotificationData();
            data.setNotificationTitle(title);
            data.setNotificationMessage(message);
            data.setNotificationId(id);
            data.setNotificationDate(task.getTaskAssigned());
            database.getReference().child(path).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
//                    Toast.makeText(context, "Notifications set in firebase", Toast.LENGTH_SHORT).show();
                }
            });


            try {
                RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
                String url = "https://fcm.googleapis.com/fcm/send";
                JSONObject Notidata = new JSONObject();
                Notidata.put("title",title);
                Notidata.put("body",message);
                JSONObject requestBody = new JSONObject();
                requestBody.put("notification", Notidata);
                requestBody.put("to", user.getToken());
                JsonObjectRequest request = new JsonObjectRequest(url, requestBody,
                        response -> {
                            // Handle the successful response
//                            Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            // Handle the error response
                            Toast.makeText(context, "Error sending notification: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }) {
                    // Set the headers for the request
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "key="+Constants.SERVER_KEY);
                        return headers;
                    }
                };
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    static public String getCurrentDateAndTime() {
        LocalDateTime myDateObj = LocalDateTime.now();
        System.out.println("Before Formatting: " + myDateObj);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E,MMM dd yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        return formattedDate;
    }

    static public String generateId() {
        return String.valueOf(new Date().getTime());
    }


    static public AlertDialog generateAlertDialog(Context context, String message, DialogInterface.OnClickListener positiveAction,
                                                  DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes", positiveAction);
        builder1.setNegativeButton(
                "No", negativeAction);
        return builder1.create();
    }

    static public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
