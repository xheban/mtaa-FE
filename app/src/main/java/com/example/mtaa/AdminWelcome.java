package com.example.mtaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class AdminWelcome extends AppCompatActivity implements View.OnClickListener{

    private Button createRestBtn, showResBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminwelcome);

        createRestBtn = (Button) findViewById(R.id.addRestaurantBtn);
        createRestBtn.setOnClickListener((View.OnClickListener) this);

        showResBtn = (Button) findViewById(R.id.showRestaurantBtn);
        showResBtn.setOnClickListener((View.OnClickListener) this);

    }

    private void toShowRest(){
        Intent intent = new Intent(this, ShowRestaurant.class);
        startActivity(intent);
    }

    private void toCreateRest(){
        Intent intent = new Intent(this, CreateRestaurant.class);
        startActivity(intent);
        //finish();
    }

    @Override
    public void onClick(View view) {
        if(view == showResBtn){
            toShowRest();
        }

        if(view == createRestBtn){
            toCreateRest();
        }
    }
}
