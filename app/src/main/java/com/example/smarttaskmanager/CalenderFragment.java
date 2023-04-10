package com.example.smarttaskmanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smarttaskmanager.Adapter.TaskAdapter;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.databinding.FragmentCalenderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalenderFragment extends Fragment {

    private CollapsibleCalendar collapsibleCalendar;
    private TaskAdapter taskAdapter;
    private String currentUserId;

    private ArrayList<Task> userTasks;
    private ArrayList<String> markedDays;

    private Date daySel = new Date();

    private FragmentCalenderBinding binding;

    private void setCalToDate(Day day){
        collapsibleCalendar.select(day);
        int calMonth = collapsibleCalendar.getMonth();
        int calYear = collapsibleCalendar.getYear();
        int selectedMonth = collapsibleCalendar.getSelectedItem().getMonth();
        int selectedYear = collapsibleCalendar.getSelectedItem().getYear();
        if((selectedMonth>calMonth&&selectedYear==calYear)||(selectedYear>calYear)){
            int monthdist = (selectedMonth-calMonth)+(12*(selectedYear-calYear));
            for(int i=0;i<monthdist;i++){
                collapsibleCalendar.nextMonth();
            }
        }
        else {
            int monthdist = (calMonth-selectedMonth)+(12*(calYear-selectedYear));
            for(int i = 0; i<monthdist; i++){
                collapsibleCalendar.prevMonth();
            }
        }
        collapsibleCalendar.expand(300);
        final Handler handler = new Handler();
        handler.postDelayed(() -> collapsibleCalendar.collapse(300), 301);

    }

    private Day getToday(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Day today = new Day(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        return today;
    }

    public CalenderFragment() {
        // Required empty public constructor

    }

    public static CalenderFragment newInstance(String param1, String param2) {
        CalenderFragment fragment = new CalenderFragment();

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
        binding = FragmentCalenderBinding.inflate(inflater,container,false);
        collapsibleCalendar = binding.calendarView;
        collapsibleCalendar.select(getToday());
        userTasks = new ArrayList<>();
        markedDays = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), userTasks, null);
        currentUserId = FirebaseAuth.getInstance().getUid();

        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDayChanged() {

            }

            @Override
            public void onClickListener() {

            }

            @Override
            public void onDaySelect() {
                Day day = collapsibleCalendar.getSelectedDay();
                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.YEAR, day.getYear());
                myCal.set(Calendar.MONTH, day.getMonth());
                myCal.set(Calendar.DAY_OF_MONTH, day.getDay());
                daySel = myCal.getTime();
                loadTask(getStringDate(daySel));
                binding.textView.setText(getStringDate(daySel));

            }

            @Override
            public void onItemClick(@NotNull View view) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int i) {

            }

        });

        binding.todayButton.setOnClickListener(v -> setCalToDate(getToday()));

        collapsibleCalendar.setSelected(true);

        binding.textView.setText(getStringDate(new Date()));
        binding.textView.setOnClickListener(view1 -> setCalToDate(collapsibleCalendar.getSelectedDay()));

        setValues();
        loadTask(getStringDate(new Date()));


        return binding.getRoot();
    }
    void loadTask(String date) {
        FirebaseDatabase.getInstance().getReference().child("all-tasks")
                .child("user-tasks")
                .child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userTasks.clear();
                        markedDays.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            assert task != null;
                            markedDays.add(getDateFromString(task.getTaskDeadline()).toString());
                            if(task.getTaskDeadline().equals(date)){
                                userTasks.add(task);
                            }
                            for(String day: markedDays){
                                LocalDate localDate = LocalDate.parse(day);
                                    int year = localDate.getYear();   // 2023
                                    int month = localDate.getMonthValue();  // 4 (April)
                                    int day2= localDate.getDayOfMonth();
                                    collapsibleCalendar.addEventTag(year,month-1,day2,R.color.secondaryColor);
                            }
                        }
                        Collections.reverse(userTasks);

                        taskAdapter.notifyDataSetChanged();
                        try {

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
        binding.userTaskRecylerView.setAdapter(taskAdapter);
        binding.userTaskRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @SuppressLint("SimpleDateFormat")
    private String getStringDate(Date date) {
        SimpleDateFormat dateFormat;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateFormat = new SimpleDateFormat("MMM d, yyyy");
        calendar.set(year, month, day);
        date = calendar.getTime();
        return dateFormat.format(date);
    }

    private LocalDate getDateFromString(String date){
        String dateString = date;
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH);
        }
        LocalDate dateconv = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateconv = LocalDate.parse(dateString, formatter);
        }
        return dateconv;
    }
}