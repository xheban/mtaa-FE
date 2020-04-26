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

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText enterUserName, enterPassword;
    private String userName, password;
    private Button loginBtn;
    private TextView forgotPasswordTextView, registerTextView;
    private ProgressDialog progressDialog;
    GlobalVariables globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        enterUserName = (EditText) findViewById(R.id.loginField);
        enterPassword = (EditText) findViewById(R.id.passwordField);

        loginBtn = (Button) findViewById(R.id.LoginBtn);
        loginBtn.setOnClickListener(this);
        loginBtn.setEnabled(false);

        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        registerTextView = (TextView) findViewById(R.id.registerTextView);

        forgotPasswordTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        globals = (GlobalVariables) getApplicationContext();

        enterUserName.addTextChangedListener(registerTextWatcher);
        enterPassword.addTextChangedListener(registerTextWatcher);
    }

    private void loginUser(){

        loginBtn.setEnabled(true);
        progressDialog.setMessage("Prihlasujem");
        progressDialog.show();
        try{
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("username", userName);
            jsonBody.put("password", password);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constats.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try{
                                JSONObject res = new JSONObject(response);
                                if(res.getString("response_code").equals("200")){
                                    JSONObject data = res.getJSONObject("response_desc");
                                    globals.setUserId(data.getString("id"));
                                    if(globals.getUserId().equals("1")){
                                        toAdmin();
                                    }
                                    else toWelocme(userName);
                                }
                                else {
                                    enterPassword.setError("Nesprávna kombinácia mena a hesla");
                                    enterPassword.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
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

    private void toRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }

    private void toForgotPassowrd() {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }

    private void toWelocme(String userName){
        Intent intent = new Intent(this, Welcome.class);
        intent.putExtra("username",userName);
        startActivity(intent);
        finish();
    }

    private void toAdmin(){
        Intent intent = new Intent(this, TestImage.class);
        //Intent intent = new Intent(this, AdminWelcome.class);
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

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userName = enterUserName.getText().toString().trim();
            password = enterPassword.getText().toString().trim();
            loginBtn.setEnabled(!userName.isEmpty() && !password.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

}
