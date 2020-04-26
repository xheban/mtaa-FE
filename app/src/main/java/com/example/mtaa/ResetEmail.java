package com.example.mtaa;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetEmail extends AppCompatActivity implements View.OnClickListener{

    private Button resetBtn;
    private EditText enterUserName, enterEmail;
    private ProgressDialog progressDialog;
    private String userName,email;
    private boolean emailReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetemail);

        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        resetBtn.setEnabled(false);

        enterUserName = (EditText) findViewById(R.id.enterUserName);
        enterEmail = (EditText) findViewById(R.id.enterEmail);

        progressDialog = new ProgressDialog(this);

        enterUserName.addTextChangedListener(registerTextWatcher);
        enterEmail.addTextChangedListener(registerTextWatcher);

        enterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(enterEmail.getText().toString().trim());
                    if(!matcher.matches()){
                        enterEmail.setError("Nesprávny formát emailu");
                        enterEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        emailReady = false;
                        resetBtn.setEnabled(false);

                    }else{
                        enterEmail.getBackground().clearColorFilter();
                        emailReady = true;
                        resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty());
                    }
                }
            }
        });
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userName = enterUserName.getText().toString().trim();
            email = enterEmail.getText().toString().trim();
            resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty() && emailReady);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };


    private void resetEmail(){

        final String userName = enterUserName.getText().toString().trim();
        final String email = enterEmail.getText().toString().trim();

        progressDialog.setMessage("Resetujem email...");
        progressDialog.show();
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", userName);
            jsonBody.put("email",email);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.RESSET_EMAIL_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            //System.out.println(response);
                            try{
                                JSONObject res = new JSONObject(response);
                                if(res.getString("response_code").equals("200")){
                                    Toast.makeText(getApplicationContext(), "Email úspešne zmenený!", Toast.LENGTH_SHORT).show();
                                    enterUserName.getBackground().clearColorFilter();
                                    finish();
                                }else{
                                    if(res.getString("response_desc").equals("Invalid username")){
                                        enterUserName.setError("Kombinácia daného užívateľského mena a emailu neexistuje");
                                        enterUserName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    }
                                    else {
                                        enterUserName.getBackground().clearColorFilter();
                                        Toast.makeText(getApplicationContext(), res.getString("email sa nepodarilo zmeniť"), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {
        if (view == resetBtn) {
            resetEmail();
        }
    }
}