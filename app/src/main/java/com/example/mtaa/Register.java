package com.example.mtaa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText enterUserName, enterName, enterLastName, enterEmail, enterPwd, enterConfirmPwd;
    private Button registerBtn;
    private ProgressDialog progressDialog;
    private String userName, name, lastName, email, password, confirmPassword;
    private boolean pwdReady,emailReady = false;
    GlobalVariables globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        globals = (GlobalVariables) getApplicationContext();

        enterUserName = findViewById(R.id.enterUserName);
        enterName = findViewById(R.id.enterName);
        enterLastName = findViewById(R.id.enterLastName);
        enterEmail = findViewById(R.id.enterEmail);
        enterPwd = findViewById(R.id.enterPwd);
        enterConfirmPwd = findViewById(R.id.enterConfirmPwd);
        registerBtn = findViewById(R.id.registerBtn);

        progressDialog = new ProgressDialog(this);
        registerBtn.setOnClickListener(this);
        registerBtn.setEnabled(false);

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
                           registerBtn.setEnabled(false);
                       }else{
                           enterConfirmPwd.setError(null);
                           enterConfirmPwd.getBackground().clearColorFilter();
                           pwdReady = true;
                           registerBtn.setEnabled(!userName.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && emailReady);
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
                        pwdReady = false;
                        registerBtn.setEnabled(false);
                    }else{
                        enterConfirmPwd.getBackground().clearColorFilter();
                        pwdReady = true;
                        registerBtn.setEnabled(!userName.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && emailReady);
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
                        emailReady = false;
                        registerBtn.setEnabled(false);

                    }else{
                        enterEmail.getBackground().clearColorFilter();
                        emailReady = true;
                        registerBtn.setEnabled(!userName.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && pwdReady);
                    }
                }
            }
        });

        enterUserName.addTextChangedListener(registerTextWatcher);
        enterName.addTextChangedListener(registerTextWatcher);
        enterLastName.addTextChangedListener(registerTextWatcher);
        enterEmail.addTextChangedListener(registerTextWatcher);
        enterPwd .addTextChangedListener(registerTextWatcher);
        enterConfirmPwd.addTextChangedListener(registerTextWatcher);
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
             userName = enterUserName.getText().toString().trim();
             name = enterName.getText().toString().trim();
             lastName = enterLastName.getText().toString().trim();
             email = enterEmail.getText().toString().trim();
             password = enterPwd.getText().toString().trim();
             confirmPassword = enterConfirmPwd.getText().toString().trim();
             registerBtn.setEnabled(!userName.isEmpty() && !name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && pwdReady && emailReady);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void registerUser(){

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

    private void toAfterRegister(){
        Intent intent = new Intent(this, AfterRegister.class);
        intent.putExtra("username",userName);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == registerBtn){
            registerUser();
        }
    }
}
