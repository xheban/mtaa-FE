package com.example.mtaa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Welcome extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private List<City> citiesArray;
    private List<String> cities;
    private Spinner citiesSpinner;
    private LinearLayout mainViewToAdd;
    private ArrayAdapter<String> adapter;
    GlobalVariables globals;

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
        globals = (GlobalVariables) getApplicationContext();
        citiesSpinner = findViewById(R.id.city_spinner);
        citiesSpinner.setOnItemSelectedListener(this);
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
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_custom, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(adapter);
        citiesSpinner.setSelection(adapter.getPosition("Bratislava"));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String valueSelected = cities.get(position);
        for (final City city : citiesArray) {
            if (city.name.equals(valueSelected) && position != 0) {
                LinearLayout mainRest = (LinearLayout) findViewById(R.id.main_restaurant_layout);
                mainRest.removeAllViews();
                citiesSpinner.setSelection(position);
                final String cityId = String.valueOf(city.id);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_ALL_RESTAURANTS_URL+"?city_id="+cityId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject res = new JSONObject(response);
                                JSONArray data = res.getJSONArray("response_desc");
                                for(int i = 0; i < data.length(); i++) {
                                    try {
                                        JSONObject restaurant = (JSONObject) data.get(i);
                                        addRestaurantToLayout(restaurant.getString("name"),
                                                restaurant.getString("food_types")+ "TODO delete",
                                                restaurant.getString("delivery_price")+" €",
                                                restaurant.getString("min_price")+" €",
                                                restaurant.getString("delivery_time")+" min",
                                                restaurant.getString("photo"));
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
            }
        }
    }

    public void addRestaurantToLayout(String name, String types, String del_price, String min_buy, String delivery_time, String photo){


        mainViewToAdd = findViewById(R.id.main_restaurant_layout);
        LinearLayout addToLayout = (LinearLayout) View.inflate(this,R.layout.restaurant_preview,null);
        ((TextView) addToLayout.findViewById(R.id.rest_title)).setText(name);
        ((TextView) addToLayout.findViewById(R.id.food_types)).setText(types);
        ((TextView) addToLayout.findViewById(R.id.delivery_price_value)).setText(del_price);
        ((TextView) addToLayout.findViewById(R.id.min_price_value)).setText(min_buy);
        ((TextView) addToLayout.findViewById(R.id.delivery_time_value)).setText(delivery_time);
        ImageView restPhoto = addToLayout.findViewById(R.id.rest_photo);

        if(photo.length() > 0){
            byte[] imgBytesData = android.util.Base64.decode(photo,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            restPhoto.setImageBitmap(bitmap);
        }else{
            restPhoto.setImageResource(R.drawable.no_image);
        }

        mainViewToAdd.addView(addToLayout);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
