package com.mk.trakit.ui.profile;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    TextView name, email, phone, cancel;
    Button  logout, update;
    String uid;
    ImageView name_edit, phone_edit, profile_pic;
    ImageButton edit_image;
    Uri uri;
    Dialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

        DatabaseReference ref = db.getReference().child("Users").child(uid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                phone.setText(user.getPhoneno());
                if(user.getProfile_pic()!=null) {
                    Glide.with(getContext()).load(user.getProfile_pic()).circleCrop().into(profile_pic);
                    profile_pic.setPadding(0,0,0,0);
                }
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();


                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures")
                                    .child(uid);

                            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isComplete());
                                    Uri urlImage = uriTask.getResult();
                                    Glide.with(getContext()).load(urlImage).circleCrop().into(profile_pic);
                                    setProfilePic(urlImage);
                                    Toast.makeText(getContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Image not uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder name_change_dialog = new AlertDialog.Builder(view.getContext());
                name_change_dialog.setTitle("Change Name");

                View dialog_view = getLayoutInflater().inflate(R.layout.name_change_dialog, null);
                TextInputEditText nameChange = dialog_view.findViewById(R.id.name);
                update = dialog_view.findViewById(R.id.update);
                cancel = dialog_view.findViewById(R.id.cancel);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nameUpdate(nameChange.getText().toString());
                        name.setText(nameChange.getText().toString());
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                name_change_dialog.setView(dialog_view);
                dialog = name_change_dialog.create();
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        phone_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder phoneno_change_dialog = new AlertDialog.Builder(view.getContext());
                phoneno_change_dialog.setTitle("Change Phone No");

                View dialog_view = getLayoutInflater().inflate(R.layout.phoneno_change_dialog, null);
                TextInputEditText phonenoChange = dialog_view.findViewById(R.id.phonenumber);
                update = dialog_view.findViewById(R.id.update);
                cancel = dialog_view.findViewById(R.id.cancel);

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phonenoUpdate(phonenoChange.getText().toString());
                        phone.setText(phonenoChange.getText().toString());
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                phoneno_change_dialog.setView(dialog_view);
                dialog = phoneno_change_dialog.create();
                dialog.setCancelable(false);
                dialog.show();

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

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                Uri dpUri = data.getData();
                setProfilePic(dpUri);



            }
        }
    }*/

    private void nameUpdate(String name){
        DatabaseReference ref = db.getReference().child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setName(name);

                ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Name updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void phonenoUpdate(String phoneno){
        DatabaseReference ref = db.getReference().child("Users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user.setPhoneno(phoneno);

                ref.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Phone No updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setProfilePic(Uri dpUri) {
        /*Bitmap bitmap = convertUriToBitmap(dpUri);
        Bitmap circularBitmap = ImageHelper.getRoundedBitmap(bitmap);
        profile_pic.setImageBitmap(circularBitmap);
        profile_pic.setPadding(0,0,0,0);*/
        StorageReference sref = storage.getReference().child("profile-pic").child(uid);

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