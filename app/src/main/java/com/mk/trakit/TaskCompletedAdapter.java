package com.mk.trakit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskCompletedAdapter extends RecyclerView.Adapter<TaskCompletedAdapter.ViewHolder> {

    private Context context;
    public List<Task> taskList;


    public TaskCompletedAdapter(Context mContext, List<Task> taskList) {
        this.context = mContext;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskCompletedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_recycler_completed, parent, false);
        return new TaskCompletedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Task item = taskList.get(position);
        holder.title.setText(item.getTask_name());
        holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if(!item.getDue_date().equals("")){
            String date = item.getDue_date();
            holder.due.setText(item.getDue_date());
            try {
                Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                Date today = Calendar.getInstance().getTime();
                if(date1.before(today)){
                    holder.due.setTextColor(Color.RED);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            holder.due.setVisibility(View.VISIBLE);
        }

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("TaskAssignments");
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                UserTask userTask = new UserTask(uid, isChecked);
                ref.child(item.getId()).child(uid).setValue(userTask);
                taskList.remove(position);
                notifyDataSetChanged();
                notifyItemRemoved(position);
                ((ToDoActivity) context).refresh();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("Name", taskList.get(holder.getAdapterPosition()).getTask_name());
                intent.putExtra("Description", taskList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Id", taskList.get(holder.getAdapterPosition()).getId());
                intent.putExtra("Due Date", taskList.get(holder.getPosition()).getDue_date());
                intent.putExtra("Time Created", taskList.get(holder.getPosition()).getTime_created());
                intent.putExtra("Room Id", taskList.get(holder.getPosition()).getRoom_id());
                intent.putExtra("User Id", taskList.get(holder.getPosition()).getUser_id());
                intent.putExtra("Completed", true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public void editItem(int position) {
        Task item = taskList.get(position);

        Intent intent = new Intent(context, TaskActivity.class);
        intent.putExtra("Name", taskList.get(position).getTask_name());
        intent.putExtra("Description", taskList.get(position).getDescription());
        intent.putExtra("Id", taskList.get(position).getId());
        intent.putExtra("Due Date", taskList.get(position).getDue_date());
        intent.putExtra("Time Created", taskList.get(position).getTime_created());
        intent.putExtra("Room Id", taskList.get(position).getRoom_id());
        intent.putExtra("User Id", taskList.get(position).getUser_id());
        intent.putExtra("Completed", false);
        context.startActivity(intent);

    }

    public void deleteItem(int position) {
        Task item = taskList.get(position);
        taskList.remove(position);
        notifyItemRemoved(position);

    }

    public Context getContext() {
        return context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView title;
        TextView due;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckbox);
            title = view.findViewById(R.id.todo_title);
            due = view.findViewById(R.id.due_date);
        }
    }
}

