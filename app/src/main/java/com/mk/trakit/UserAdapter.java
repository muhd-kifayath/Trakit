package com.mk.trakit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> userList;
    private FirebaseUser currentUser;
    private String item;
    public UserAdapter(Context mContext, List<User> userList) {
        this.mContext = mContext;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_recycler,parent,false);
        item="";
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = userList.get(position);
        if(currentUser.getUid()!=user.getId()) {
            holder.txtUserName.setText(user.getName());
            Glide.with(mContext).load(user.getProfile_pic()).circleCrop().into(holder.imgProfilePic);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                intent.putExtra("userName", user.getName());
                intent.putExtra("userPictureUrl", user.getProfile_pic());
                mContext.startActivity(intent);
            }
        });
            /*holder.btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("userName", user.getName());
                    intent.putExtra("userPictureUrl", user.getProfile_pic());
                    mContext.startActivity(intent);
                }
            });
            if (user.getId().equals(currentUser.getUid())) {
                holder.btnMessage.setVisibility(View.INVISIBLE);
            }*/
        }



    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtUserName;
        ImageView imgProfilePic;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.user_content_username);
            imgProfilePic = itemView.findViewById(R.id.user_content_profilePicture);

        }
    }
}
