package com.mk.trakit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {

    EditText enterTextChat;
    ImageView imageProfileChat, receiverUserPic;
    TextView recieverUserName;
    ImageButton sendMessage;
    FirebaseUser currentUser;

    private long pressedTime;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private String userId, userName, userPictureUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        /*Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        receiverUserPic = findViewById(R.id.image_receiver_profile_chat);
        recieverUserName = findViewById(R.id.receiver_userName_chat);

        enterTextChat = findViewById(R.id.enterTextChat);
        sendMessage = findViewById(R.id.send_chat);

        recyclerView = findViewById(R.id.recycler_view_chats);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userPictureUrl = intent.getStringExtra("userPictureUrl");

        Glide.with(MessageActivity.this).load(userPictureUrl).circleCrop().into(receiverUserPic);
        recieverUserName.setText(userName);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterTextChat.getText().toString().equals("")){
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                else{
                    addMessageMethod();
                }
            }
        });
        readMessages();

        Log.d("Size", "Chat List size: "+chatList.size());
    }

    private void addMessageMethod(){
        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        String currentDate = dateFormat.format(date.getTime());

        Calendar time= Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        String currentTime = timeFormat.format(time.getTime());

        DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("Messages");
        String chatId = referenceChat.push().getKey();
        Chat chat = new Chat();
        chat.setMessageText(enterTextChat.getText().toString());
        chat.setReceiverUserId(userId);
        chat.setSenderUserId(currentUser.getUid());
        chat.setCurrentDate(currentDate);
        chat.setCurrentTime(currentTime);

        /*hashMap.put("receiverUserName",userName);
        hashMap.put("receiverUserPictureUrl",userPictureUrl);
        hashMap.put("senderUserName",userPictureUrl);
        hashMap.put("senderUserPictureUrl",userPictureUrl);*/

        assert chatId != null;
        referenceChat.child(chatId).setValue(chat);
        //referenceChat.updateChildren(hashMap);
        enterTextChat.setText("");
    }
    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(getApplicationContext()).load(user.getProfile_pic()).circleCrop().into(imageProfileChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);


                    assert chat != null;
                    if(chat.getReceiverUserId().equals(currentUser.getUid())&& chat.getSenderUserId().equals(userId)||
                            chat.getReceiverUserId().equals(userId) && chat.getSenderUserId().equals(currentUser.getUid()) )
                    {
                        chatList.add(chat);
                    }


                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}