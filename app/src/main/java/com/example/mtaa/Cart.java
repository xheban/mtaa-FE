package com.example.mtaa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Cart  extends AppCompatActivity {
    private GlobalVariables globals;
    private TextView sum;
    private Button order;
    private double totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        sum = findViewById(R.id.sum_of_cart);
        globals = (GlobalVariables) getApplicationContext();
        order = findViewById(R.id.button_order);
        order.setEnabled(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_CART_URL +"?user_id="+globals.getUserId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("response_code").equals("200")){
                                if(res.getString("response_desc").equals("empty")){
                                    sum.setText("- €");
                                    order.setEnabled(false);
                                }else{
                                    JSONArray data = res.getJSONArray("response_desc");
                                    for(int i = 0; i < data.length(); i++) {
                                        try {
                                            JSONObject food = (JSONObject) data.get(i);
                                            totalSum += food.getDouble("price");
                                            addFoodToCartLayout( food.getString("name")+": ", food.getString("price")+" €", food.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    order.setEnabled(true);
                                    String toShow = totalSum + " €";
                                    sum.setText(toShow);
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

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vo) {
                order.setEnabled(false);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.ORDER_URL+"?user_id="+globals.getUserId(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    if(res.getString("response_code").equals("200")){
                                            sum.setText("- €");
                                            order.setEnabled(false);
                                            Toast.makeText(getApplicationContext(),"Objednávka prebehla úspešne", Toast.LENGTH_SHORT).show();
                                            LinearLayout mainRest = (LinearLayout) findViewById(R.id.main_layout_cart);
                                            mainRest.removeAllViews();
                                    }else{
                                        order.setEnabled(true);
                                        Toast.makeText(getApplicationContext(),"Nepodarilo sa spraccovať objednávku", Toast.LENGTH_SHORT).show();
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
    }

    public void addFoodToCartLayout(String name, final String price, final String id){
        final LinearLayout mainViewToAdd = findViewById(R.id.main_layout_cart);
        final LinearLayout addToLayout = (LinearLayout) View.inflate(this,R.layout.cart_item,null);
        ((TextView) addToLayout.findViewById(R.id.food_name_cart)).setText(name);
        ((TextView) addToLayout.findViewById(R.id.food_price_cart)).setText(price);
        final Button deleteFromCart = addToLayout.findViewById(R.id.delete_from_cart);
        deleteFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vo) {
                final StringRequest stringRequest = new StringRequest(Request.Method.DELETE, Constats.DELETE_FROM_CART_URL +"?user_id="+globals.getUserId()+"&food_id="+id,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    System.out.println(id);
                                    System.out.println(res);
                                    if(res.getString("response_code").equals("200")){
                                        double priceD = Double.parseDouble(price.substring(0, price.length() - 2));
                                        totalSum -= priceD;
                                        if(totalSum == 0){
                                            sum.setText("- €");
                                            order.setEnabled(false);
                                        }else{
                                            String textToShow = totalSum+" €";
                                            sum.setText(textToShow);
                                        }
                                        mainViewToAdd.removeView(addToLayout);
                                        Toast.makeText(getApplicationContext(),"Jedlo bolo odstránene z košíka", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Jedlo sa nepodarilo odstrániť z košíka", Toast.LENGTH_SHORT).show();
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

        mainViewToAdd.addView(addToLayout);
    }
}
