package com.mk.trakit.ui.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mk.trakit.Chat;
import com.mk.trakit.DatabaseHelper;
import com.mk.trakit.R;
import com.mk.trakit.User;
import com.mk.trakit.UserAdapter;
import com.mk.trakit.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    EditText enterTextChat;
    ImageView imageProfileComment, receiverUserPic;
    TextView sendMessage, receiverUserName;
    FirebaseUser currentUser;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private String userId, userName, userPictureUrl;
    EditText search;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view_searchUser);
        search = root.findViewById(R.id.search);
        // searchTxtUserArea = (EditText) findViewById(R.id.search_text_area);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter= new UserAdapter(getContext(),userList);
        recyclerView.setAdapter(userAdapter);
        allUsersRead();

        return root;
    }

    private void allUsersRead()
    {
        DatabaseReference userRef = DatabaseHelper.getUsers();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                userRef.orderByChild("name").startAt(charSequence.toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        userList.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            User user = snapshot.getValue(User.class);
                            String id = "";
                            if (user != null) {
                                id = user.getId();
                            }
                            if(!id.equals(currentUser.getUid())){
                                Log.d("Verify User", id);
                                userList.add(user);
                                Log.d("Room List", userList.toString());
                            }
                        }
                        userAdapter.notifyDataSetChanged();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}