package com.mk.trakit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class ToDoActivity extends AppCompatActivity {

    RecyclerView pendingTasks, completedTasks;
    ExtendedFloatingActionButton addTask;
    Button create;
    TextView cancel;

    String room_name, rid, imageUrl, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        pendingTasks = findViewById(R.id.pending_tasks);
        completedTasks = findViewById(R.id.completed_tasks);
        addTask = findViewById(R.id.addtask);

        Intent intent = getIntent();
        room_name = intent.getStringExtra("Title");
        rid = intent.getStringExtra("Id");
        imageUrl = intent.getStringExtra("Image");

        uid = FirebaseAuth.getInstance().getUid();

        Dialog dialog = new Dialog(ToDoActivity.this,R.style.DialogStyle);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.create_task_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                EditText taskName = dialog.findViewById(R.id.task_name);
                EditText duedate = dialog.findViewById(R.id.due_date);
                EditText description = dialog.findViewById(R.id.description);

                TextInputLayout dateLayout = dialog.findViewById(R.id.textField1);

                dateLayout.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar calendar=Calendar.getInstance();
                        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR,year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                                duedate.setText(dateFormat.format(calendar.getTime()));
                            }
                        };
                        DatePickerDialog datePickerDialog=new DatePickerDialog(dialog.getContext(),R.style.DateTimeDialogTheme,onDateSetListener,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                        datePickerDialog.setTitle("Pick Due Date");
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);

                        datePickerDialog.show();

                    }
                });

                create = dialog.findViewById(R.id.create);
                cancel = dialog.findViewById(R.id.cancel);

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String task_name = taskName.getText().toString();
                        String due_date = duedate.getText().toString();
                        String desc = description.getText().toString();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        Calendar calendar = Calendar.getInstance();
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
                        String time_created = dateFormat.format(calendar.getTime());

                        String tid =  UUID.randomUUID().toString();

                        Task task = new Task(tid, task_name, desc, rid, due_date, time_created, uid);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tasks").child(tid);

                        reference.setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ToDoActivity.this, "Task Created!", Toast.LENGTH_SHORT).show();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("TaskAssignments");
                                UserTask userTask = new UserTask(FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
                                ref.child(tid).setValue(userTask);
                                dialog.dismiss();
                            }
                        });

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(ToDoActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });

    }
}