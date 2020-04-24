package com.example.mtaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AfterRegister extends AppCompatActivity implements View.OnClickListener {

    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_registration);

        continueBtn = (Button) findViewById(R.id.btn_continuu_to_acc);
        continueBtn.setOnClickListener(this);
    }

    private void toWelcome(){
        Intent intent = new Intent(this, Welcome.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == continueBtn) {
            toWelcome();
        }
    }
}