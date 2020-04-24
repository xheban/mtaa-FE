package com.example.mtaa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText enterUserName, enterPassword;
    private Button loginBtn;
    private TextView forgotPasswordTextView, registerTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        enterUserName = (EditText) findViewById(R.id.loginField);
        enterPassword = (EditText) findViewById(R.id.passwordField);

        loginBtn = (Button) findViewById(R.id.LoginBtn);
        loginBtn.setOnClickListener(this);

        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        registerTextView = (TextView) findViewById(R.id.registerTextView);

        forgotPasswordTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    private void loginUser(){

        final String userName = enterUserName.getText().toString().trim();
        String userPassword = enterPassword.getText().toString().trim();

        if(!userName.isEmpty() && !userPassword.isEmpty()){
            progressDialog.setMessage("Prihlasujem");
            progressDialog.show();
            try{
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", userName);
                jsonBody.put("password", userPassword);
                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.LOGIN_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try{
                                    JSONObject res = new JSONObject(response);
                                    //Toast.makeText(getApplicationContext(), res.getString("response_desc"), Toast.LENGTH_LONG).show();
                                    if(res.getString("response_code").equals("200")) toWelocme(userName);
                                    else Toast.makeText(getApplicationContext(), res.getString("response_desc"), Toast.LENGTH_LONG).show();

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
        else Toast.makeText(getApplicationContext(), "Vyplň všetky polia!", Toast.LENGTH_LONG).show();

    }

    private void toRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }

    private void toForgotPassowrd() {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
        //finish();
    }

    private void toWelocme(String userName){
            Intent intent = new Intent(this, Welcome.class);
            //intent.putExtra("username",userName);
            startActivity(intent);
            finish();
    }

    @Override
    public void onClick(View view) {
        if(view == loginBtn){
            loginUser();
        }

        if(view == forgotPasswordTextView){
            toForgotPassowrd();
        }

        if(view == registerTextView){
            toRegister();
        }
    }

}
