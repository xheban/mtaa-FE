package com.example.mtaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AfterRegister extends AppCompatActivity implements View.OnClickListener{
    private Button continueBtn;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getIntent().getStringExtra("username");
        setContentView(R.layout.after_registration);
        continueBtn = (Button) findViewById(R.id.btn_continuu_to_acc);
        continueBtn.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view) {
        if(view == continueBtn){
            toWelcome(userName);
        }
    }

    private void toWelcome(String userName){
        Intent intent = new Intent(this, Welcome.class);
        intent.putExtra("username",userName);
        startActivity(intent);
        finish();
    }
}

