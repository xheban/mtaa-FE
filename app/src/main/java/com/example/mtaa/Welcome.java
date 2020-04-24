package com.example.mtaa;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        //String Name = getIntent().getStringExtra("username");
        welcomeText = (TextView) findViewById(R.id.welcomeTitle);

        welcomeText.setText("Welcome ");
    }
}
