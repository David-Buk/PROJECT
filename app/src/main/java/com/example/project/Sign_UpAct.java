package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_UpAct extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtAge;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtAge = findViewById(R.id.edtAge);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String ageStr = edtAge.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(ageStr)) {
                    Toast.makeText(Sign_UpAct.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageStr);

                registerUser(username, password, age);
            }
        });
    }

    private void registerUser(String username, String password, int age) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            writeNewUser(userId, username, age);

                            Toast.makeText(Sign_UpAct.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Sign_UpAct.this, Log_InAct.class));
                        }
                    } else {
                        Toast.makeText(Sign_UpAct.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void writeNewUser(String userId, String username, int age) {
        User user = new User(username, age);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public static class User {
        public String username;
        public int age;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }
}
