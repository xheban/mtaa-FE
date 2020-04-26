package com.example.mtaa;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {

    private Button resetBtn;
    private EditText enterUserName, enterEmail, enterPassword, enterConfirmPassword;
    private String userName, email, password, confirmPassword;
    private ProgressDialog progressDialog;
    private boolean pwdReady,emailReady = false;
    private TextView forgotEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);

        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        resetBtn.setEnabled(false);

        enterUserName = (EditText) findViewById(R.id.enterUserName);
        enterPassword = (EditText) findViewById(R.id.enterPwd);
        enterConfirmPassword = (EditText) findViewById(R.id.enterConfirmPwd);
        enterEmail = (EditText) findViewById(R.id.enterEmail);

        progressDialog = new ProgressDialog(this);

        forgotEmailTextView = findViewById(R.id.forgotEmailTextView);
        forgotEmailTextView.setOnClickListener(this);

        enterUserName.addTextChangedListener(registerTextWatcher);
        enterEmail.addTextChangedListener(registerTextWatcher);
        enterPassword .addTextChangedListener(registerTextWatcher);
        enterConfirmPassword.addTextChangedListener(registerTextWatcher);

        enterPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if(enterPassword.length() < 6){
                    enterPassword.setError("Heslo musí obsahovať najmenej 6 znakov");
                    enterPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }else{
                    enterPassword.getBackground().clearColorFilter();
                }
                if(enterConfirmPassword.length() > 0){
                    if(!enterPassword.getText().toString().trim().equals(enterConfirmPassword.getText().toString().trim())){
                        enterConfirmPassword.setError("Hesla sa nezhodujú");
                        enterConfirmPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        resetBtn.setEnabled(false);
                    }else{
                        enterConfirmPassword.setError(null);
                        enterConfirmPassword.getBackground().clearColorFilter();
                        pwdReady = true;
                        resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && emailReady);
                    }
                }
            }
            }
        });

        enterConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!enterPassword.getText().toString().trim().equals(enterConfirmPassword.getText().toString().trim())){
                        enterConfirmPassword.setError("Hesla sa nezhodujú");
                        enterConfirmPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        pwdReady = false;
                        resetBtn.setEnabled(false);
                    }else{
                        enterConfirmPassword.getBackground().clearColorFilter();
                        pwdReady = true;
                        resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && emailReady);
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
                        enterEmail.setError("Nesprávny formát emailu");
                        enterEmail.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                        emailReady = false;
                        resetBtn.setEnabled(false);

                    }else{
                        enterEmail.getBackground().clearColorFilter();
                        emailReady = true;
                        resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && pwdReady);
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
            password = enterPassword.getText().toString().trim();
            confirmPassword = enterConfirmPassword.getText().toString().trim();
            resetBtn.setEnabled(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && pwdReady && emailReady);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void resetPassword() {
        final String userName = enterUserName.getText().toString().trim();
        final String userPassword = enterPassword.getText().toString().trim();
        final String confirmPassword = enterConfirmPassword.getText().toString().trim();
        final String email = enterEmail.getText().toString().trim();

        if (userPassword.equals(confirmPassword)) {
            progressDialog.setMessage("Resetujem heslo...");
            progressDialog.show();
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", userName);
                jsonBody.put("new_password", userPassword);
                jsonBody.put("email", email);
                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.RESSET_PASSWORD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject res = new JSONObject(response);
                                System.out.println(res);
                                if (res.getString("response_code").equals("200")) {
                                    Toast.makeText(getApplicationContext(), "Heslo úspešne zmenené!", Toast.LENGTH_SHORT).show();
                                    enterUserName.getBackground().clearColorFilter();
                                    finish();
                                } else {
                                    if(res.getString("response_desc").equals("!exist")){
                                        enterUserName.setError("Zadané užívateľské meno neexistuje");
                                        enterUserName.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                                    }else{
                                        enterUserName.getBackground().clearColorFilter();
                                        Toast.makeText(getApplicationContext(), res.getString("Heslo sa nepodarilo zmeniť"), Toast.LENGTH_SHORT).show();
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
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println(error);
                        }
                    }) {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Hesla sa nezhoduju!", Toast.LENGTH_LONG).show();
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