package project.application.hotelbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button signinbtn;
    private TextInputEditText username,password;
    String uname,upass;
    protected LocationRequest locationRequest;

    private Boolean isuserloggedin;
    private TextView click;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_login);
        signinbtn = findViewById(R.id.btn_login);
        click = findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
        if(!isConnected(LoginActivity.this)){
            buildDialog2(LoginActivity.this).show();
        }
        username = findViewById(R.id.input_name);
        password = findViewById(R.id.input_password);
        signinbtn = findViewById(R.id.btn_login);

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();
            }
        });


    }
    public AlertDialog.Builder buildDialog2(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data/Wifi to access.");

        waitforsometime();

        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                finish();
            }
        }, 2000);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                super.onBackPressed();
                break;
        }


        return true;
    }
    public void loginuser(){
        uname = username.getText().toString();
        upass = password.getText().toString();
        boolean isError = false;
        if(uname.isEmpty() || uname.equals("")){
            username.setError("Field Cannot be empty");
            isError = true;
        }else if(upass.isEmpty() || upass.equals("")){
            password.setError("Field Cannot be empty");
            isError = true;
        }

        if(!isError){
            dialog = ProgressDialog.show(LoginActivity.this, "Please Wait",
                    "checking credentials...");
            dialog.show();
VolleyRequest();
                   }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void VolleyRequest(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        String url = Urls.LOGIN_ACCOUNT;
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
                                buildDialog(LoginActivity.this,"502","Internal Server Error").show();
                            }
                            else if(status.equals("404")){
                                buildDialog(LoginActivity.this,"Access Deined","username/password is wrong").show();
                            }else if(status.equals("200")){
                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                String username = reader.getString("username");
                                String password = reader.getString("password");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isuserloggedin",true);
                                editor.putString("username",username);
                                editor.putString("password",password);

                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,DashBoardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                buildDialog(LoginActivity.this,"Ooopps..!","Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", uname);
                params.put("password", upass);
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

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        return builder;
    }
    }
