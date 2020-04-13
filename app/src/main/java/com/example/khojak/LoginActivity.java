package com.example.khojak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onSignUpPressed(View view){
        Toast.makeText(this,"Sign up pressed", Toast.LENGTH_SHORT).show();
    }


    public void onLoginPressed(View view){
        Toast.makeText(this,"Login pressed", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, HomePage.class));
    }
}
