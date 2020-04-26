package com.example.mtaa;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RestaurantDetail extends AppCompatActivity{
    private String name, types, deliveryPrice, minBuy, deliveryTime, photo, id, openFrom, openTo, rating;
    private TextView textViewName, textViewDeliveryPrice, textViewMinBuy, textViewDeliveryTime, textViewOpenFrom, textViewOpenTo, textViewRating;
    private ImageView imageTitleRestaurantDetail;
    private LinearLayout mainViewToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail);
        name = getIntent().getStringExtra("name");
        types = getIntent().getStringExtra("types");
        deliveryPrice = getIntent().getStringExtra("deliveryPrice");
        minBuy = getIntent().getStringExtra("minBuy");
        deliveryTime = getIntent().getStringExtra("deliveryTime");
        photo = getIntent().getStringExtra("photo");
        id = getIntent().getStringExtra("id");
        openFrom = getIntent().getStringExtra("openFrom");
        openTo = getIntent().getStringExtra("openTo");
        rating = getIntent().getStringExtra("rating");

        textViewName = findViewById(R.id.rest_detail_title);
        textViewDeliveryPrice = findViewById(R.id.delivery_price_value_detail);
        textViewMinBuy = findViewById(R.id.min_price_value_detail);
        textViewDeliveryTime = findViewById(R.id.delivery_time_value_detail);
        textViewOpenFrom = findViewById(R.id.open_from_value);
        textViewOpenTo = findViewById(R.id.open_to_value);
        textViewRating = findViewById(R.id.rating_value);
        imageTitleRestaurantDetail = findViewById(R.id.rest_det_img);

        textViewName.setText(name);
        textViewDeliveryPrice.setText(deliveryPrice);
        textViewDeliveryTime.setText(deliveryTime);
        textViewMinBuy.setText(minBuy);
        textViewOpenFrom.setText(openFrom);
        textViewOpenTo.setText(openTo);
        textViewRating.setText(rating);

        if(photo.length() > 0){
            byte[] imgBytesData = android.util.Base64.decode(photo, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            imageTitleRestaurantDetail.setImageBitmap(bitmap);
        }else{
            imageTitleRestaurantDetail.setImageResource(R.drawable.no_image);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_FOOD_FOR_RESTAURANT+"?restaurant_id="+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONArray data = res.getJSONArray("response_desc");
                            for(int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject food = (JSONObject) data.get(i);
                                    addFoodToLayout(food.getString("name"),
                                             food.getString("ingredients"),
                                       food.getString("price")+" €",
                                      food.getString("weight")+" g",
                                             food.getString("photo")
                                    );
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

    public void addFoodToLayout(String name, String ingredients, String price, String weight, String photo){
        mainViewToAdd = findViewById(R.id.main_restaurant_detail_layout);
        LinearLayout addToLayout = (LinearLayout) View.inflate(this,R.layout.food_preview,null);
        ((TextView) addToLayout.findViewById(R.id.food_name)).setText(name);
        ((TextView) addToLayout.findViewById(R.id.ingredients)).setText(ingredients);
        ((TextView) addToLayout.findViewById(R.id.price_value)).setText(price);
        ((TextView) addToLayout.findViewById(R.id.weight_value)).setText(weight);
        ImageView food_photo = addToLayout.findViewById(R.id.food_photo);
        Button addToCart = addToLayout.findViewById(R.id.add_to_cart);

        if(photo.length() > 0){
            byte[] imgBytesData = android.util.Base64.decode(photo,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytesData, 0, imgBytesData.length);
            food_photo.setImageBitmap(bitmap);
        }else{
            food_photo.setImageResource(R.drawable.no_image);
        }

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vo) {
              //TODO add to cart
            }
        });
        mainViewToAdd.addView(addToLayout);
    }
}
