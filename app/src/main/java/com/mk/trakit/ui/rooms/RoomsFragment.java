package com.mk.trakit.ui.rooms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mk.trakit.LoginActivity;
import com.mk.trakit.MainActivity;
import com.mk.trakit.R;
import com.mk.trakit.RegisterActivity;
import com.mk.trakit.Room;
import com.mk.trakit.User;
import com.mk.trakit.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RoomsFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseDatabase db;
    Button create, addmember;
    TextView cancel;
    int count;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RoomsViewModel roomsViewModel =
                new ViewModelProvider(this).get(RoomsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        db = FirebaseDatabase.getInstance();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add);
        Dialog dialog = new Dialog(getActivity(),R.style.DialogStyle);
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        count = 1;

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
                        HashMap<Integer, User> members = new HashMap<Integer, User>();
                        Room room = new Room();
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
                                    members.put(i,getUser(member[i].getText().toString()));
                                }
                            }
                            if(n==count){
                                room.setId(UUID.randomUUID().toString());
                                String rid = room.getId();
                                DatabaseReference reference = db.getReference().child("Rooms").child(rid);
                                for (int i=0;i< member.length;i++){
                                    Log.d("member: "+i,members.get(i).getId());
                                }
                                room.setMember(members);

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
                        Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });

        return view;
    }

    public User getUser(String email){
        DatabaseReference ref = db.getReference().child("Users");
        final User[] newuser = {new User()};
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if(user.getEmail().equals(email)) {
                        newuser[0] = user;
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

        return newuser[0];
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}