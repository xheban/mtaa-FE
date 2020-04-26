package com.example.mtaa;
import android.os.Bundle;
import android.view.View;
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

public class Profile extends AppCompatActivity {

    private String userName;
    private TextView userNameTitle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        userName = getIntent().getStringExtra("username");
        GlobalVariables globals = (GlobalVariables) getApplicationContext();
        userNameTitle = findViewById(R.id.profile_user_name);
        userNameTitle.setText(userName);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_FOOD_HISTORY_URL +"?user_id="+globals.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("response_code").equals("200")){
                                JSONArray data = res.getJSONArray("response_desc");
                                for(int i = 0; i < data.length(); i++) {
                                    try {
                                        JSONObject food = (JSONObject) data.get(i);
                                        addToOrderHistory( food.getString("time")+"  - ", food.getString("price")+" €", food.getJSONArray("food_list"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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

    public void addToOrderHistory(String time, String price, JSONArray food){
        final LinearLayout mainViewToAdd = findViewById(R.id.main_history_layout);
        final LinearLayout addToLayout = (LinearLayout) View.inflate(this,R.layout.food_history_order_item,null);
        ((TextView) addToLayout.findViewById(R.id.date_of_order)).setText(time);
        ((TextView) addToLayout.findViewById(R.id.sum_of_order)).setText(price);

        for(int i = 0; i < food.length(); i++) {
            try {
                JSONObject foodRecord = (JSONObject) food.get(i);

                LinearLayout whereToAddFood = addToLayout.findViewById(R.id.food_adding_layout);
                LinearLayout addToFoodLayout = (LinearLayout) View.inflate(this,R.layout.food_history_food_item, null);
                String foodName = foodRecord.getString("name")+": ";
                String foodPrice = foodRecord.getString("price")+" €";
                ((TextView) addToFoodLayout.findViewById(R.id.food_name_history_order)).setText(foodName);
                ((TextView) addToFoodLayout.findViewById(R.id.food_price_history_order)).setText(foodPrice);
                whereToAddFood.addView(addToFoodLayout);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        mainViewToAdd.addView(addToLayout);
    }
}
