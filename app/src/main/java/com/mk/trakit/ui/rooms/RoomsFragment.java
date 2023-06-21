package com.mk.trakit.ui.rooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mk.trakit.R;
import com.mk.trakit.databinding.FragmentHomeBinding;

public class RoomsFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseDatabase db;
    Button create, addmember;
    TextView cancel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RoomsViewModel roomsViewModel =
                new ViewModelProvider(this).get(RoomsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add);
        Dialog dialog = new Dialog(getActivity());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.create_room_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                create = dialog.findViewById(R.id.create);
                cancel = dialog.findViewById(R.id.cancel);

                addmember = dialog.findViewById(R.id.addmember);

                RelativeLayout mRlayout = (RelativeLayout) view.findViewById(R.id.create_room_dialog);
                RelativeLayout.LayoutParams mRparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                addmember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextInputLayout textInputLayout = new TextInputLayout(getContext());
                        LinearLayout.LayoutParams eParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        EditText myEditText = new EditText(getContext());
                        myEditText.setLayoutParams(eParams);

                        textInputLayout.setId(R.id.member2);
                        mRparams.setMargins(0,20,0,0);

                        textInputLayout.addView(myEditText);
                        textInputLayout.setLayoutParams(mRparams);
                        mRlayout.addView(textInputLayout);
                    }
                });


                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "okay clicked", Toast.LENGTH_SHORT).show();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}