package com.mk.trakit.ui.rooms;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mk.trakit.R;
import com.mk.trakit.Room;
import com.mk.trakit.RoomActions;
import com.mk.trakit.RoomAdapter;
import com.mk.trakit.User;
import com.mk.trakit.UserTask;
import com.mk.trakit.databinding.FragmentRoomsBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RoomsFragment extends Fragment {

    private FragmentRoomsBinding binding;
    FirebaseDatabase db;
    Button create, addmember;
    TextView cancel;
    private RecyclerView roomRecycler;
    private List<Room> roomList;
    int count;
    FirebaseUser currentUser;

    RoomAdapter roomAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRoomsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        db = FirebaseDatabase.getInstance();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add);
        Dialog dialog = new Dialog(getActivity(),R.style.DialogStyle);
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        count = 1;
        roomRecycler = view.findViewById(R.id.room_list);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        roomRecycler.setHasFixedSize(true);
        roomRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        roomList = new ArrayList<>();
        roomAdapter= new RoomAdapter(getContext(),roomList);
        roomRecycler.setAdapter(roomAdapter);

        ItemTouchHelper itemTouchHelperPending = new
                ItemTouchHelper(new RoomActions(roomAdapter));
        itemTouchHelperPending.attachToRecyclerView(roomRecycler);

        allRoomRead();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.create_room_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


                int padding10 = (int) (10 * scale + 0.5f);
                int padding15 = (int) (15 * scale + 0.5f);
                int padding50 = (int) (50 * scale + 0.5f);

                create = dialog.findViewById(R.id.create);
                cancel = dialog.findViewById(R.id.cancel);

                addmember = dialog.findViewById(R.id.addmember);
                String member1Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                EditText email1 = dialog.findViewById(R.id.email);
                if(member1Email!=null) {
                    email1.setText(member1Email);
                }
                else{
                    email1.setFocusable(true);
                }

                RelativeLayout mRlayout = (RelativeLayout) dialog.findViewById(R.id.membersRel);

                RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                addmember.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint({"ResourceAsColor", "ResourceType"})
                    @Override
                    public void onClick(View view) {
                        count++;
                        if (count>10){
                            Toast.makeText(getContext(), "Maximum 10 members",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            ViewGroup.LayoutParams eParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            mRparams.addRule(RelativeLayout.BELOW, R.id.textFieldEmail);
                            mRparams.setMargins(0, (count-2)*170, 0,0);

                            TextInputLayout textInputLayout = new TextInputLayout(getContext());
                            textInputLayout.setBoxBackgroundColor(R.color.transparent);
                            textInputLayout.setBoxStrokeColor(R.color.md_theme_light_primary);

                            textInputLayout.setLayoutParams(mRparams);
                            textInputLayout.setHint("Member " + count);
                            textInputLayout.setHintTextAppearance(R.color.hint_text);



                            EditText myEditText = new EditText(getContext());
                            myEditText.setLayoutParams(eParams);
                            myEditText.setBackgroundResource(R.drawable.edittextbg);
                            myEditText.setPadding(padding15, padding15, padding15, padding15);
                            myEditText.setTextSize(6 * scale + 0.5f);
                            myEditText.setMaxLines(1);
                            myEditText.setTag("member"+count);
                            myEditText.setId(200+count);
                            myEditText.setInputType(32);
                            myEditText.setNextFocusDownId(mRlayout.getNextFocusDownId());

                            //textInputLayout.setId(R.id.member2+count);

                            textInputLayout.addView(myEditText);
                            mRlayout.addView(textInputLayout);
                        }
                    }
                });

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText roomName = dialog.findViewById(R.id.room_name);
                        String room_name = roomName.getText().toString();
                        EditText[] member = new EditText[count];
                        member[0] = dialog.findViewById(R.id.email);
                        Room room = new Room();
                        String[] memberEmails = new String[count];
                        for(int i=1;i<count;i++){
                            member[i] = dialog.findViewById(201+i);
                        }
                        if(TextUtils.isEmpty(room_name)){
                            roomName.setError("Enter Room Name");
                        }
                        else{
                            room.setRoom_name(room_name);
                            int n=0;
                            for(int i=0;i<count;i++){
                                if(TextUtils.isEmpty(member[i].getText().toString())){
                                    member[i].setError("Member email should not be empty!");
                                }
                                else{
                                    n++;
                                }
                            }
                            if(n==count){
                                room.setId(UUID.randomUUID().toString());
                                String rid = room.getId();
                                DatabaseReference reference = db.getReference().child("Rooms").child(rid);
                                for (int i=0;i< member.length;i++){
                                    setUser(member[i].getText().toString(),rid);
                                    memberEmails[i] = member[i].getText().toString();

                                }
                                sendMail(memberEmails, room_name);

                                reference.setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Room created successfully.", Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"Posted are Failed\n"+e,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        count = 1;
                        Toast.makeText(getActivity(), "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });

        return view;
    }


    private void sendMail(String[] emails, String roomName) {
        int n = emails.length;
        String emailsend[] = emails;
        Log.d("Emails", emails.toString());
        emailsend = Arrays.copyOfRange(emailsend, 1,n);
        String emailsubject = "Added to new Room in Trakit";
        String emailbody = String.format("You have been added to %s, open app to check new tasks!", roomName);


        // define Intent object with action attribute as ACTION_SEND
        Intent intent = new Intent(Intent.ACTION_SEND);

        // add three fields to intent using putExtra function
        intent.putExtra(Intent.EXTRA_EMAIL, emailsend);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject);
        intent.putExtra(Intent.EXTRA_TEXT, emailbody);

        // set type of intent
        intent.setType("message/rfc822");
        startActivity(intent);
    }

    private void allRoomRead()
    {
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Rooms");
        DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("RoomMembers");
        roomRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if(searchTxtUserArea.getText().toString().equals(""))

                roomList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Room room = snapshot.getValue(Room.class);

                    memberRef.child(room.getId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot1) {
                            for(DataSnapshot data:dataSnapshot1.getChildren()){
                                UserTask userTask = data.getValue(UserTask.class);
                                String id = "";
                                if (userTask != null) {
                                    id = userTask.getId();
                                }
                                if(id.equals(currentUser.getUid())){
                                    Log.d("Verify User", id);
                                    roomList.add(room);
                                    Log.d("Room List", roomList.toString());
                                    roomAdapter.notifyDataSetChanged();
                                    break;
                                }

                            }
                        }
                    });

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUser(String email, String room_id){
        DatabaseReference ref = db.getReference().child("Users");
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    User user = data.getValue(User.class);

                    if(user.getEmail().equals(email)) {
                        String id = user.getId();
                        UserTask userTask = new UserTask(id, true);
                        DatabaseReference utReference = db.getReference().child("RoomMembers").child(room_id).child(id);
                        utReference.setValue(userTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Member added to room", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Couldn't fetch data\n"+e,Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}