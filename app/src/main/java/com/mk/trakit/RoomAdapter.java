package com.mk.trakit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private Context context;
    public List<Room> roomList;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_recycler, parent, false);
        return new RoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recTitle.setText(roomList.get(position).getRoom_name());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ToDoActivity.class);
                intent.putExtra("Image", roomList.get(holder.getAdapterPosition()).getImage());
                intent.putExtra("Title", roomList.get(holder.getAdapterPosition()).getRoom_name());
                intent.putExtra("Id", roomList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void editItem(int position) {
        Room item = roomList.get(position);

        Intent intent = new Intent(context, ToDoActivity.class);
        intent.putExtra("Image", roomList.get(position).getImage());
        intent.putExtra("Title", roomList.get(position).getRoom_name());
        intent.putExtra("Id", roomList.get(position).getId());
        context.startActivity(intent);
    }

    public void deleteItem(int position) {
        Room item = roomList.get(position);
        roomList.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void searchDataList(ArrayList<Room> searchList){
        roomList = searchList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView recTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recTitle = itemView.findViewById(R.id.recTitle);
        }
    }
}
