package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Sign_UpAct extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtAge;
    private Button btnRegister;

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDbHelper = new DbHelper(this, "myDB", null, 1);

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

                if (username.isEmpty() || password.isEmpty() || ageStr.isEmpty()) {
                    Toast.makeText(Sign_UpAct.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageStr);
                Intent intent = new Intent(Sign_UpAct.this, Log_InAct.class);
                intent.putExtra("user_age", age);
                startActivity(intent);
                // Register the user
                mDbHelper.register(username, password, age);
                Toast.makeText(Sign_UpAct.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                // Redirect to Login activity
                startActivity(new Intent(Sign_UpAct.this, Log_InAct.class));
            }
        });
    }
}