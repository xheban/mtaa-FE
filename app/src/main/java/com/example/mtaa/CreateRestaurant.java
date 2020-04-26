package com.example.mtaa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CreateRestaurant extends AppCompatActivity implements View.OnClickListener {

    EditText enterName, enterPrice, enterMinPrice, enterTime;
    Spinner citySpinner, openFrom, openTo;
    Button addFoodBtn, createResBtn;
    ImageView photo;

    final int CODE_GALERY_REQUEST = 999;

    private List<City> citiesArray;
    private List<String> cities;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter1;

    public class City {
        public int id;
        public String name;

        City(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createrestaurant);

        enterName = (EditText) findViewById(R.id.enterName);
        enterPrice = (EditText) findViewById(R.id.enterPrice);
        enterMinPrice = (EditText) findViewById(R.id.enterPriceMin);
        enterTime = (EditText) findViewById(R.id.enterTime);

        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        openFrom = (Spinner) findViewById(R.id.open_from);
        openTo = (Spinner) findViewById(R.id.open_to);

        addFoodBtn = (Button) findViewById(R.id.createFoodBtn);
        createResBtn = (Button) findViewById(R.id.createFoodBtn);

        addFoodBtn.setOnClickListener(this);
        createResBtn.setOnClickListener(this);

        photo = (ImageView)findViewById(R.id.imageView7);
        photo.setOnClickListener(this);

        String[] values = new String[]{
          "01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00",
                "13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","00:00"
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, values);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        openTo.setAdapter(adapter1);
        openFrom.setAdapter(adapter1);

        citiesArray =  new ArrayList<City>();
        cities = new ArrayList<String>();
        cities.add(0, "Zvoľte mesto");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_CITIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("response_desc");
                            for(int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject city = (JSONObject) data.get(i);
                                    citiesArray.add(new City(city.getInt("id"),city.getString("name")));
                                    cities.add(city.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }
        );
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_custom, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        Toast.makeText(getApplicationContext(), cities.size() + " " + citiesArray.size(), Toast.LENGTH_LONG).show();

    }

    private void toAddFood(){

            Intent intent = new Intent(this, CreateFood.class);
            //intent.putExtra("username",userName);
            startActivity(intent);
            //finish();
    }

    @Override
    public void onClick(View view) {
        if(view == addFoodBtn){
            toAddFood();
        }
    }


}
