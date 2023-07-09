package com.mk.trakit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mk.trakit.R;
import com.mk.trakit.Room;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Room> dataList;

    public RoomAdapter(Context context, List<Room> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recTitle.setText(dataList.get(position).getRoom_name());


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getImage());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getRoom_name());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Room> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView recTitle;
        CardView recCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recCard = itemView.findViewById(R.id.recCard);
            recTitle = itemView.findViewById(R.id.recTitle);
        }
    }
}
