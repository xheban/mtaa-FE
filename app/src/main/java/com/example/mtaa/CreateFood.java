package com.example.mtaa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CreateFood extends AppCompatActivity implements View.OnClickListener {

    EditText enterName, enterPrice, enterIngr, enterWeight;
    Button createFoodBtn,backBtn;
    ImageView imageView;
    int PICK_IMAGE_REQUEST = 111;
    Bitmap bitmap;
    Spinner type_spinner;

    private List<foodType> foodTypesArray;
    private List<String> foods;
    private ArrayAdapter<String> adapter;

    public class foodType {
        public int id;
        public String name;

        foodType(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createfood);

        enterName = (EditText) findViewById(R.id.enterName);
        enterPrice = (EditText) findViewById(R.id.enterPrice);
        enterIngr = (EditText) findViewById(R.id.enterIngr);
        enterWeight = (EditText) findViewById(R.id.enterWeight);

        createFoodBtn = (Button) findViewById(R.id.createFoodBtn);
        createFoodBtn.setOnClickListener(this);

        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.imageView7);
        imageView.setOnClickListener(this);

        type_spinner = (Spinner) findViewById(R.id.type_spinner);

        foodTypesArray =  new ArrayList<foodType>();
        foods = new ArrayList<String>();
        foods.add(0, "Zvoľte typ");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_TYPES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("response_desc");
                            for(int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject foodt = (JSONObject) data.get(i);
                                    foodTypesArray.add(new foodType(foodt.getInt("id"),foodt.getString("name")));
                                    foods.add(foodt.getString("name"));
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

        adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_custom, foods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);

    }

    private void createFood(){

            String restaurant_id = "1";
            String name, price, ingredients, weight, imgstring, type_id;

            //progressDialog.setMessage("Registrujem");
            //progressDialog.show();
        JSONObject jsonBody = new JSONObject();
        //jsonBody.put("username", userName);
        //jsonBody.put("name", name);
        //jsonBody.put("lastname", lastName);
        //jsonBody.put("email", email);
        //jsonBody.put("password", password);
        final String mRequestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try{
                            JSONObject res = new JSONObject(response);
                            int response_code = res.getInt("response_code");
                            if(response_code == 200) {
                                //toAfterRegister();
                            }
                            if(response_code == 400){
                                JSONObject data = res.getJSONObject("response_desc");
                                if(data.getBoolean("email")){
                                    //enterEmail.setError("Tento email je už používaný");
                                    //enterEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                }else{
                                    //enterEmail.getBackground().clearColorFilter();
                                }

                                if(data.getBoolean("username")){
                                    //enterUserName.setError("Toto užívateľské meno je už obsadené");
                                    //enterUserName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                }else{
                                    //enterUserName.getBackground().clearColorFilter();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
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

    private void loadImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onClick(View view) {
        if(view == backBtn){
            finish();
        }

        if(view == createFoodBtn){
            createFood();
        }

        if(view == imageView){
            loadImage();
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
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
