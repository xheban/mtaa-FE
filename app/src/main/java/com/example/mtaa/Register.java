package com.example.mtaa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText enterUserName, enterName, enterLastName, enterEmail, enterPwd, enterConfirmPwd;
    private Button registerBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        enterUserName = (EditText) findViewById(R.id.enterUserName);
        enterName = (EditText) findViewById(R.id.enterName);
        enterLastName = (EditText) findViewById(R.id.enterLastName);
        enterEmail = (EditText) findViewById(R.id.enterEmail);
        enterPwd = (EditText) findViewById(R.id.enterPwd);
        enterConfirmPwd = (EditText) findViewById(R.id.enterConfirmPwd);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);

        registerBtn.setOnClickListener(this);

        enterPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                   if(enterPwd.length() < 6){
                       enterPwd.setError("Heslo musí obsahovať najmenej 6 znakov");
                       enterPwd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                   }else{
                       enterPwd.getBackground().clearColorFilter();
                   }
                   if(enterConfirmPwd.length() > 0){
                       if(!enterPwd.getText().toString().trim().equals(enterConfirmPwd.getText().toString().trim())){
                           enterConfirmPwd.setError("Hesla sa nezhodujú");
                           enterConfirmPwd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                       }else{
                           enterConfirmPwd.setError(null);
                           enterConfirmPwd.getBackground().clearColorFilter();
                       }
                   }
                }
            }
        });

        enterConfirmPwd.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!enterPwd.getText().toString().trim().equals(enterConfirmPwd.getText().toString().trim())){
                        enterConfirmPwd.setError("Hesla sa nezhodujú");
                        enterConfirmPwd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    }else{
                        enterConfirmPwd.getBackground().clearColorFilter();
                    }
                }
            }
        });

        enterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(enterEmail.getText().toString().trim());
                    if(!matcher.matches()){
                        enterEmail.setError("Nesprávny formát emailu ");
                        enterEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                    }else{
                        enterEmail.getBackground().clearColorFilter();
                    }
                }
            }
        });
    }

    private void registerUser(){
        String userName = enterUserName.getText().toString().trim();
        String name = enterName.getText().toString().trim();
        String lastName = enterLastName.getText().toString().trim();
        String email = enterEmail.getText().toString().trim();
        String password = enterPwd.getText().toString().trim();
        String confirmPassword = enterConfirmPwd.getText().toString().trim();

        if(password.equals(confirmPassword)){
            progressDialog.setMessage("Registrujem");
            progressDialog.show();
            try{
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", userName);
                jsonBody.put("name", name);
                jsonBody.put("lastname", lastName);
                jsonBody.put("email", email);
                jsonBody.put("password", password);
                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try{
                                JSONObject res = new JSONObject(response);
                                int response_code = res.getInt("response_code");
                                if(response_code == 200) {
                                    toAfterRegister();
                                }
                                if(response_code == 400){
                                    JSONObject data = res.getJSONObject("response_desc");
                                    if(data.getBoolean("email")){
                                        enterEmail.setError("Tento email je už používaný");
                                        enterEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    }else{
                                        enterEmail.getBackground().clearColorFilter();
                                    }

                                    if(data.getBoolean("username")){
                                        enterUserName.setError("Toto užívateľské meno je už obsadené");
                                        enterUserName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    }else{
                                        enterUserName.getBackground().clearColorFilter();
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
                            progressDialog.hide();
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


    }

    private void test(){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constats.GET_ALL_RESTAURANTS_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        Toast.makeText(getApplicationContext(), res.getString("response_code"), Toast.LENGTH_LONG).show();
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
    }

    private void toAfterRegister(){
        Intent intent = new Intent(this, AfterRegister.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == registerBtn){
//            test();
            registerUser();
        }
    }
}
