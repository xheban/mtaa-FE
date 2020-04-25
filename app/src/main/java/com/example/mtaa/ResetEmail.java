package com.example.mtaa;

import android.app.ProgressDialog;
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

public class ResetEmail extends AppCompatActivity implements View.OnClickListener{

    private Button resetBtn;
    private EditText enterUserName, enterEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetemail);

        resetBtn = (Button) findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);

        enterUserName = (EditText) findViewById(R.id.enterUserName);
        enterEmail = (EditText) findViewById(R.id.enterEmail);

        progressDialog = new ProgressDialog(this);


    }

    private void resetEmail(){

        final String userName = enterUserName.getText().toString().trim();
        final String email = enterEmail.getText().toString().trim();

        if(!userName.isEmpty()  && !email.isEmpty()){
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
                                    //Toast.makeText(getApplicationContext(), res.getString("response_desc"), Toast.LENGTH_LONG).show();
                                    if(res.getString("response_code").equals("200")){
                                        Toast.makeText(getApplicationContext(), "Email úspešne zmenené!", Toast.LENGTH_LONG).show();

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
            Toast.makeText(getApplicationContext(), "Vyplň všetky polia!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view == resetBtn) {
            resetEmail();
        }
    }
}