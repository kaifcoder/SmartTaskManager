package com.example.smarttaskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.smarttaskmanager.Adapter.TaskAdapter;
import com.example.smarttaskmanager.Models.Task;
import com.example.smarttaskmanager.databinding.FragmentChartBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChartFragment extends Fragment {

    private FragmentChartBinding binding;
    private ArrayList<Task> userTasks;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChartBinding.inflate(inflater,container,false);
        PieChart pieChart = binding.piechart;
        pieChart.getDescription().setEnabled(false);
        userTasks = new ArrayList<>();
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(90f);
        pieChart.setHoleColor(getResources().getColor(R.color.white));
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setTouchEnabled(false);

        pieChart.animateY(1000, Easing.EaseInOutCubic);

        binding.piename.setText("Completion Rate");


        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadTask();
    }

    void loadTask() {
        FirebaseDatabase.getInstance().getReference().child("all-tasks")
                .child("user-tasks")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int todo = 0;
                        int inProgress = 0;
                        int done = 0;
                        int all;
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
                        all = todo+inProgress+done;
                        // Set pie chart data
                        ArrayList<Integer> colors = new ArrayList<>();
                        colors.add(R.color.primaryDarkColor);
                        colors.add(R.color.colorAccent);
                        ArrayList<PieEntry> values = new ArrayList<>();
                        values.add(new PieEntry(done, "Completed"));
                        values.add(new PieEntry(all-done, "Not Completed"));
                        PieDataSet dataSet = new PieDataSet(values, "");
                        dataSet.setColors(colors);
                        dataSet.setDrawValues(false);
                        PieData pieData = new PieData(dataSet);
                        binding.piechart.setData(pieData);
                        binding.piechart.invalidate();
                        binding.piename.setText("Completion Rate");
                        binding.rate.setText("Completed: "+done);
                      }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}