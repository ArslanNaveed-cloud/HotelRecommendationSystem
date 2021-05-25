package project.application.hotelbookingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    private TextInputEditText email;
    private String email1="";
    private Button btn;
    private boolean isError=true;
    private ProgressDialog dialog;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.check_emil);
        btn = findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemail();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        if(sharedPreferences.getString("username", "")!=null ||!(sharedPreferences.getString("username", "").isEmpty())){
            username = sharedPreferences.getString("username", "");
        }
        if(username != null){
            username = sharedPreferences.getString("username", "");

        }
    }
    public void checkemail(){
        email1 = email.getText().toString().trim();
        if(email1.isEmpty()){
            email.setError("Field cannot be empty");
            isError = true;
        }


        else if(!(Patterns.EMAIL_ADDRESS.matcher(email1).matches())){
            email.setError("Invalid Email Address");
            isError = true;
        }else{
            email.setError(null);
            isError = false;
        }
        if(!isError){
            dialog = ProgressDialog.show(ForgotPassword.this, "Please Wait",
                    "checking email...");
            dialog.show();
                SendEmailVolley();
        }
    }
    public void SendEmailVolley(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ForgotPassword.this);

        String url = Urls.FORGOT_PASSWORD;
        Log.i("112233",url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if(status.equals("500")){
                                buildDialog(ForgotPassword.this,"502","Internal Server Error").show();
                            }
                            else if(status.equals("404")){
                                buildDialog(ForgotPassword.this,"Access Deined","Invalid Email").show();
                            }else if(status.equals("200")){
                                buildDialog(ForgotPassword.this,"Password Changed","Check your email address").show();
                               waitforsometime();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                buildDialog(ForgotPassword.this,"Ooopps..!","Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email1);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);



        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(ForgotPassword.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}