package com.example.travelwell_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginAdminActivity extends AppCompatActivity {

    EditText adminlogin_username, adminlogin_password;
    Button adminlogin_button;
    TextView userLoginRedirectText, reTextadminSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        adminlogin_username = findViewById(R.id.adminlogin_username);
        adminlogin_password = findViewById(R.id.adminlogin_password);
        adminlogin_button = findViewById(R.id.adminlogin_button);
        userLoginRedirectText = findViewById(R.id.userLoginRedirectText);
        reTextadminSignup = findViewById(R.id.reTextadminSignup);


        adminlogin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateAdminUsername() | !validateAdminPassword()){

                }else {
                    checkAdmin();
                }
            }
        });

        userLoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        reTextadminSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, SignupAdminActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean validateAdminUsername(){
        String val = adminlogin_username.getText().toString();
        if (val.isEmpty()){
            adminlogin_username.setError("Admin Username cannot be empty");
            return false;
        } else {
            adminlogin_username.setError(null);
            return true;
        }
    }

    public boolean validateAdminPassword(){
        String val = adminlogin_password.getText().toString();
        if (val.isEmpty()){
            adminlogin_password.setError("Admin Password cannot be empty");
            return false;
        } else {
            adminlogin_password.setError(null);
            return true;
        }
    }

    public void checkAdmin(){
        String adminUsername = adminlogin_username.getText().toString().trim();
        String adminPassword = adminlogin_password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admins");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(adminUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    adminlogin_username.setError(null);
                    String passwordFromDB = snapshot.child(adminUsername).child("password").getValue(String.class);

                    if(passwordFromDB.equals(adminPassword)){
                        adminlogin_username.setError(null);

                        //Pass the data using intent

                        String nameFromDB = snapshot.child(adminUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(adminUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(adminUsername).child("username").getValue(String.class);


                        Intent intent = new Intent(LoginAdminActivity.this, HomeAdminActivity.class);

                        intent.putExtra("name",nameFromDB);
                        intent.putExtra("email",emailFromDB);
                        intent.putExtra("username",usernameFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);


                    } else {
                        adminlogin_password.setError("Invalid Credentials");
                        adminlogin_password.requestFocus();
                    }
                } else {
                    adminlogin_username.setError("User does not Exist");
                    adminlogin_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}