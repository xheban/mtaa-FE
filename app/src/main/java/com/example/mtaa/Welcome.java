package com.example.mtaa;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Welcome<ObjectMapper> extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView welcomeText;
    private List<City> citiesArray;
    private List<String> cities;
    private Spinner citiesSpinner;
    private boolean deleteDefault = true;

    public class City {
        public int id;
        public String name;

        City(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        String Name = getIntent().getStringExtra("username");

        citiesSpinner = findViewById(R.id.city_spinner);
        citiesSpinner.setOnItemSelectedListener(this);
        citiesArray =  new ArrayList<City>();
        cities = new ArrayList<String>();
        cities.add(0, "Zvoľte mesto");

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_CITIES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("response_desc");
                            System.out.println(data.get(0));
                            for(int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject city = (JSONObject) data.get(i);
                                    citiesArray.add( new City(city.getInt("id"),city.getString("name")));
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
        queue.add(stringRequest);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout., cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String valueSelected = cities.get(position);
        if(deleteDefault){
            cities.remove(0);
            deleteDefault = false;
        }
        for (City city : citiesArray) {
            if (city.name.equals(valueSelected)) {
                // TODO reload restaurands by city ID
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
