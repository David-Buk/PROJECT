package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Log_InAct extends AppCompatActivity {
    private Button btnLogin;
    private TextView txtVRegister;
    private EditText edtUsername, edtPassword;

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mDbHelper = new DbHelper(this, "myDB", null, 1);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtVRegister = findViewById(R.id.txtVRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fill in Your Details", Toast.LENGTH_SHORT).show();
                } else {
                    int loginResult = mDbHelper.login(username, password);
                    if (loginResult == 1) {
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity
                        startActivity(new Intent(Log_InAct.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtVRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Log_InAct.this, Sign_UpAct.class));
                Toast.makeText(getApplicationContext(), "Registering New User", Toast.LENGTH_SHORT).show();
            }
        });
    }
}