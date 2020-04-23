package com.example.mtaa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
                                Toast.makeText(getApplicationContext(), res.getString("response_code"), Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View view) {
        if(view == registerBtn){
//            test();
            registerUser();
        }
    }
}
