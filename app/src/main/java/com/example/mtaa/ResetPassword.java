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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private Button resetBtn;
    private EditText enterUserName, enterEmail, enterPassword, enterConfirmPaswwrod;
    private ProgressDialog progressDialog;
    private TextView forgotEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);

        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);

        enterUserName = (EditText) findViewById(R.id.enterUserName);
        enterPassword = (EditText) findViewById(R.id.enterPwd);
        enterConfirmPaswwrod = (EditText) findViewById(R.id.enterConfirmPwd);
        enterEmail = (EditText) findViewById(R.id.enterEmail);

        progressDialog = new ProgressDialog(this);

        forgotEmailTextView = findViewById(R.id.forgotEmailTextView);
        forgotEmailTextView.setOnClickListener(this);
    }

    private void resetPassword(){
        final String userName = enterUserName.getText().toString().trim();
        final String userPassword = enterPassword.getText().toString().trim();
        final String confirmPassword = enterConfirmPaswwrod.getText().toString().trim();
        final String email = enterEmail.getText().toString().trim();

        if(!userName.isEmpty() && !userPassword.isEmpty() && !confirmPassword.isEmpty() && !email.isEmpty()){
            if(userPassword.equals(confirmPassword)){
                progressDialog.setMessage("Resetujem heslo...");
                progressDialog.show();
                try{
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", userName);
                    jsonBody.put("new_password", userPassword);
                    jsonBody.put("email",email);
                    final String mRequestBody = jsonBody.toString();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.RESSET_PASSWORD_URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    //System.out.println(response);
                                    try{
                                        JSONObject res = new JSONObject(response);
                                        //Toast.makeText(getApplicationContext(), res.getString("response_desc"), Toast.LENGTH_LONG).show();
                                        if(res.getString("response_code").equals("200")){
                                            Toast.makeText(getApplicationContext(), "Heslo úspešne zmenené!", Toast.LENGTH_LONG).show();
                                            
                                            finish();
                                        }
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

            }else{
                Toast.makeText(getApplicationContext(), "Hesla sa nezhoduju!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Vyplň všetky polia!", Toast.LENGTH_LONG).show();
        }

    }

    private void toForgotEmail(){
            Intent intent = new Intent(this, ResetEmail.class);
            startActivity(intent);
            //finish();
        }


    @Override
    public void onClick(View view) {
        if (view == resetBtn) {
            resetPassword();
        }

        if(view == forgotEmailTextView){
            toForgotEmail();
        }
    }
}