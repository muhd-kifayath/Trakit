package com.mk.trakit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {
    String taskName, dueDate, desc, tid, rid, cid, created;
    Boolean completed;

    EditText task_name, description;
    LinearLayout duedateBox;
    TextView duedate, createComplete, cancel;
    CheckBox checkBox;
    ImageButton delete;
    Dialog dialog;
    Button delete_task;
    private long pressedTime;
    final String create = "Created on ";
    final String complete = "Completed on ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        taskName = intent.getStringExtra("Name");
        desc = intent.getStringExtra("Description");
        dueDate = intent.getStringExtra("Due Date");
        tid = intent.getStringExtra("Id");
        rid = intent.getStringExtra("Room Id");
        cid = intent.getStringExtra("User Id");
        created = intent.getStringExtra("Time Created");
        completed = intent.getBooleanExtra("Completed", false);


        task_name = findViewById(R.id.task_name);
        duedateBox = findViewById(R.id.duedatebox);
        duedate = findViewById(R.id.due_date);
        checkBox = findViewById(R.id.todoCheckbox);
        description = findViewById(R.id.description);
        createComplete = findViewById(R.id.createComplete);
        delete = findViewById(R.id.delete);


        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(cid.equals(currentUser)){
            delete.setVisibility(View.VISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DatabaseReference ref = DatabaseHelper.getTaskAssignments();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                UserTask userTask = new UserTask(uid, checkBox.isChecked());
                ref.child(tid).child(uid).setValue(userTask);
                task_name.setPaintFlags(task_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        task_name.setText(taskName);
        if(!dueDate.equals("")){
            String due = "Due on "+dueDate;
            duedate.setText(due);
            duedate.setTextColor(getColor(R.color.md_theme_light_primary));
            Date date1 = null;
            try {
                date1 = new SimpleDateFormat("dd-MM-yyyy").parse(dueDate);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Date today = Calendar.getInstance().getTime();
            if(date1.before(today)){
                duedate.setTextColor(Color.RED);
            }
        }
        if(!desc.equals("")){
            description.setText(desc);
        }
        if(completed){
            String text = complete+created;
            createComplete.setText(text);
        }
        else{
            String text = create+created;
            createComplete.setText(text);
        }
        task_name.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String str = s.toString();
                if(TextUtils.isEmpty(str)){
                    task_name.setError("Task Name cannot be empty!");
                }
                else {
                    DatabaseReference reference = DatabaseHelper.getTasks();
                    reference.child(tid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Task task = snapshot.getValue(Task.class);
                            task.setTask_name(str);
                            reference.child(tid).setValue(task);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                DatabaseReference reference = DatabaseHelper.getTasks();
                reference.child(tid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Task task = snapshot.getValue(Task.class);
                        task.setDescription(str);
                        reference.child(tid).setValue(task);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        duedateBox.setOnClickListener(new View.OnClickListener() {
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
                        String due = "Due on "+dateFormat.format(calendar.getTime());
                        duedate.setText(due);

                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(TaskActivity.this,R.style.DateTimeDialogTheme,onDateSetListener,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setTitle("Pick Due Date");
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);

                datePickerDialog.show();
            }
        });


    }


}