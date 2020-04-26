package com.example.mtaa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

public class ShowRestaurant extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private List<CitySR> citiesArray;
    private List<String> cities;
    private Spinner citiesSpinner;
    private LinearLayout mainViewToAdd;
    private ArrayAdapter<String> adapter;

    @Override
    public void onClick(View v) {

    }

    public class CitySR {
        public int id;
        public String name;

        CitySR(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showrestauant);

        citiesSpinner = findViewById(R.id.city_spinner_admin);
        citiesSpinner.setOnItemSelectedListener(this);
        citiesArray =  new ArrayList<CitySR>();
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
                                    citiesArray.add( new CitySR(city.getInt("id"),city.getString("name")));
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String valueSelected = cities.get(position);
        for (final CitySR city : citiesArray) {
            if (city.name.equals(valueSelected) && position != 0) {
                LinearLayout mainRest = (LinearLayout) findViewById(R.id.main_admin_rest_layout);
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
                                                    restaurant.getString("photo"),
                                                    restaurant.getString("id"));

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

    public void addRestaurantToLayout(final String name, final String photo, final String id){

        final LinearLayout mainViewToAdd = findViewById(R.id.main_admin_rest_layout);
        final LinearLayout addToLayout = (LinearLayout) View.inflate(this,R.layout.admin_restaurant_preview,null);
        ((TextView) addToLayout.findViewById(R.id.rest_title_admin)).setText(name);
        ImageView restPhoto = addToLayout.findViewById(R.id.rest_photo_admin);
        Button detail = addToLayout.findViewById(R.id.btn_detail);
        Button delete = addToLayout.findViewById(R.id.btn_delete);

        if(photo.length() > 0){
            byte[] imgBytesData = android.util.Base64.decode(photo, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            restPhoto.setImageBitmap(bitmap);
        }else{
            restPhoto.setImageResource(R.drawable.no_image);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vo) {
                final StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constats.DELETE_RESTAURANT_URL+"?id="+id,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    System.out.println(id);
                                    System.out.println(res);
                                    if(res.getString("response_code").equals("200")){
                                        Toast.makeText(getApplicationContext(),"Reštaurácia odstránená", Toast.LENGTH_SHORT).show();
                                        mainViewToAdd.removeView(addToLayout);
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Reštauráciu sa nepodarilo odstrániť", Toast.LENGTH_SHORT).show();
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
                RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vo) {
                toAddFood(id);
            }
        });


        mainViewToAdd.addView(addToLayout);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void toAddFood(String restaurant_id){
        Intent intent = new Intent(this, CreateFood.class);
        intent.putExtra("restaurant_id",restaurant_id);
        startActivity(intent);
    }

}
