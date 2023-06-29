package com.mk.trakit.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.mk.trakit.ImageHelper;
import com.mk.trakit.MainActivity;
import com.mk.trakit.R;
import com.mk.trakit.User;
import com.mk.trakit.databinding.FragmentProfileBinding;

import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    FirebaseDatabase db;
    TextView name, email, phone;
    Button  logout;
    String uid;

    ImageView name_edit, phone_edit, profile_pic;
    ImageButton edit_image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseDatabase.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final User[] user = {new User()};
        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.phone);

        name_edit = root.findViewById(R.id.name_edit_btn);
        phone_edit = root.findViewById(R.id.phone_edit_btn);
        profile_pic = root.findViewById(R.id.user_image);

        edit_image = root.findViewById(R.id.edit_image_btn);

        DatabaseReference ref = db.getReference().child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user[0] = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        name.setText(user[0].getName());
        email.setText(user[0].getEmail());
        phone.setText(user[0].getPhoneno());
        if(user[0].getProfile_pic()!=null) {
            setProfilePic(Uri.parse(user[0].getProfile_pic()));
            Toast.makeText(getContext(), "DP not null!", Toast.LENGTH_SHORT).show();
        }
        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 100);

            }
        });

        name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder name_change_dialog = new AlertDialog.Builder(view.getContext());
                name_change_dialog.setTitle("Change Name");
                name_change_dialog.setMessage("Enter Name: ");
                //name_change_dialog.setPositiveButton()
            }
        });

        phone_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        logout = (Button) root.findViewById(R.id.logout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((MainActivity) getActivity()).signOut();
                }
                catch(Exception e){
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                Uri dpUri = data.getData();
                setProfilePic(dpUri);
                DatabaseReference ref = db.getReference().child("Users").child(uid);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        user.setProfile_pic(dpUri.toString());

                        ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    private void setProfilePic(Uri dpUri) {
        Bitmap bitmap = convertUriToBitmap(dpUri);
        Bitmap circularBitmap = ImageHelper.getRoundedBitmap(bitmap);
        profile_pic.setImageBitmap(circularBitmap);
        profile_pic.setPadding(0,0,0,0);
    }

    public Bitmap convertUriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            ContentResolver resolver = getActivity().getContentResolver();

            // Set the options for decoding with inSampleSize
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Only decode the bounds, not the actual bitmap
            InputStream inputStream = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate the inSampleSize value
            options.inSampleSize = ImageHelper.calculateInSampleSize(options, 500, 500);

            // Decode the bitmap with the calculated inSampleSize
            options.inJustDecodeBounds = false; // Decode the actual bitmap
            inputStream = resolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}