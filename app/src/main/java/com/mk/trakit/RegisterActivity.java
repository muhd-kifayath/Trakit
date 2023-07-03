package com.mk.trakit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    EditText user_name, mail, password, confirm_pass;
    Button register;
    TextView login;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name = findViewById(R.id.name);
        mail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_pass = findViewById(R.id.confirm_pass);
        register = findViewById(R.id.registerbtn);
        login = findViewById(R.id.account_exists);

        auth= FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = user_name.getText().toString();
                String email = mail.getText().toString();
                String pass = password.getText().toString();
                String confirmpass = confirm_pass.getText().toString();

                if(TextUtils.isEmpty(name)){
                    user_name.setError("Cannot leave Mail ID empty");
                }

                if(TextUtils.isEmpty(email)){
                    mail.setError("Cannot leave Mail ID empty");
                }
                else{
                    String[] split = new String[0];
                    split = email.split("@");
                    String domain = split[1];
                }

                if(TextUtils.isEmpty(pass)||TextUtils.isEmpty(confirmpass)){
                    if(TextUtils.isEmpty(pass)){
                        password.setError("Enter a password");
                    }
                    else{
                        confirm_pass.setError("Passwords do not match!");
                    }
                }
                else if(!pass.equals(confirmpass)){
                    confirm_pass.setError("Passwords do not match!");
                }
                else{
                    addUserToDb(name,email,pass);
                }
            }
        });

    }
    private void addUserToDb(final String name, final String email, String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            assert currentUser != null;
                            String id = currentUser.getUid();
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            reference = db.getReference().child("Users").child(id);
                            String phoneno = "";
                            String photourl = "";

                            User user = new User();
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhoneno(phoneno);
                            user.setProfile_pic(photourl);

                            reference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this,"Posted are Failed\n"+e,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        else{
                            //progressbar.dismiss();
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed.", Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }
}