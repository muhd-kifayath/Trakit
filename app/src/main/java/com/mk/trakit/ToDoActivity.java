package com.mk.trakit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ToDoActivity extends AppCompatActivity {

    RecyclerView pendingTasks, completedTasks;
    FloatingActionButton addTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        pendingTasks = findViewById(R.id.pending_tasks);
        completedTasks = findViewById(R.id.completed_tasks);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}