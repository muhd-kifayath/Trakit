package com.mk.trakit.ui.profile;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mk.trakit.ImageHelper;
import com.mk.trakit.MainActivity;
import com.mk.trakit.R;
import com.mk.trakit.User;
import com.mk.trakit.databinding.FragmentProfileBinding;

import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private static final int REQUEST_CODE = 9273;
    private static final String PREF_NAME = "MyAppPrefs";
    private static final String PERMISSION_PREF_KEY = "StoragePermission";
    FirebaseDatabase db;
    FirebaseStorage storage;
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
        storage = FirebaseStorage.getInstance();

        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        phone = root.findViewById(R.id.phone);

        name_edit = root.findViewById(R.id.name_edit_btn);
        phone_edit = root.findViewById(R.id.phone_edit_btn);
        profile_pic = root.findViewById(R.id.user_image);

        edit_image = root.findViewById(R.id.edit_image_btn);

        if (!hasStoragePermission()) {
            requestStoragePermission();
        }

        StorageReference storageReference = storage.getReference();

        DatabaseReference ref = db.getReference().child("Users").child(uid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                phone.setText(user.getPhoneno());
                if(user.getProfile_pic()!=null) {
                    setProfilePic(Uri.parse(user.getProfile_pic()));
                    Toast.makeText(getContext(), "DP not null!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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



    // Method to check if storage permission is granted
    private boolean hasStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    // Method to request storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access storage
            } else {
                // Permission denied, handle accordingly (e.g., show an error message or disable certain functionality)
            }
        }
    }




    // Method to store the permission preference
    private void storePermissionPreference(boolean granted) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PERMISSION_PREF_KEY, granted);
        editor.apply();
    }

    // Method to check if the permission was previously granted
    private boolean hasPermissionPreference() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PERMISSION_PREF_KEY, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}