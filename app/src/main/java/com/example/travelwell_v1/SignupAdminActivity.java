package com.example.travelwell_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupAdminActivity extends AppCompatActivity {

    EditText adminsignup_name, adminsignup_email, adminsignup_username, adminsignup_password;

    Button adminsignup_button;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin);

        adminsignup_name = findViewById(R.id.adminsignup_name);
        adminsignup_email = findViewById(R.id.adminsignup_email);
        adminsignup_username = findViewById(R.id.adminsignup_username);
        adminsignup_password = findViewById(R.id.adminsignup_password);
        adminsignup_button = findViewById(R.id.adminsignup_button);

        adminsignup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("admins");

                String adminname = adminsignup_name.getText().toString();
                String adminemail = adminsignup_email.getText().toString();
                String adminusername = adminsignup_username.getText().toString();
                String adminpassword = adminsignup_password.getText().toString();

                HelperClass helperClass = new HelperClass(adminname,adminemail,adminusername,adminpassword);
                reference.child(adminusername).setValue(helperClass);

                Toast.makeText(SignupAdminActivity.this, "You have signup as Admin Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupAdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}