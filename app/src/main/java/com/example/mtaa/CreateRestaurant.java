package com.example.mtaa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class CreateRestaurant extends AppCompatActivity implements View.OnClickListener {

    EditText enterName, enterPrice, enterMinPrice, enterTime;
    Spinner citySpinner, openFrom, openTo;
    Button  createResBtn;
    ImageView photo;
    Bitmap bitmap;

    public int createdFood = 0;

    int PICK_IMAGE_REQUEST = 111;

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

        citySpinner = (Spinner) findViewById(R.id.city_spinner_admin);
        openFrom = (Spinner) findViewById(R.id.open_from);
        openTo = (Spinner) findViewById(R.id.open_to);

        createResBtn = (Button) findViewById(R.id.createRestaurantBtn);

        createResBtn.setOnClickListener(this);

        photo = (ImageView)findViewById(R.id.imageView7);
        photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

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

        //Toast.makeText(getApplicationContext(), cities.size() + " " + citiesArray.size(), Toast.LENGTH_LONG).show();

    }

    private void toAddFood(){
            Intent intent = new Intent(this, CreateFood.class);
            startActivity(intent);
            //finish();
    }

    private void createRes(){

        final String food_id  = "1";
        String name, delivery_time, min_price, delivery_price;
        int city_id;
        String from,to;

        name = enterName.getText().toString().trim();
        delivery_time = enterTime.getText().toString().trim();
        delivery_price = enterPrice.getText().toString().trim();
        min_price = enterMinPrice.getText().toString().trim();

        city_id = citySpinner.getSelectedItemPosition();
        from = openFrom.getSelectedItem().toString();
        to = openTo.getSelectedItem().toString();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imgstring = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("food_id", food_id);
            jsonBody.put("name", name);
            jsonBody.put("delivery_price", delivery_price);
            jsonBody.put("delivery_time", delivery_time);
            jsonBody.put("min_price", min_price);
            jsonBody.put("photo", imgstring);
            jsonBody.put("city_id", city_id);
            jsonBody.put("from", from);
            jsonBody.put("to",to);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.CREATE_RES_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject res = new JSONObject(response);
                                int response_code = res.getInt("response_code");
                                if(response_code == 200) {
                                    Toast.makeText(getApplicationContext(), "Reštaurácia úspešne vytvorená", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                if(response_code == 400){
                                    Toast.makeText(getApplicationContext(), "Reštauráciu sa nepodarilo vytvoriť", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
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
                    }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public byte[] getBody() {
                    return mRequestBody.getBytes(StandardCharsets.UTF_8);
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        if(view == createResBtn){
            createRes();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                photo.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
