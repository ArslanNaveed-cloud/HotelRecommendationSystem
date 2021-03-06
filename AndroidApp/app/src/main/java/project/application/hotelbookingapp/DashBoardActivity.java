package project.application.hotelbookingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.Dash;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    LocationManager locationManager;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    public DrawerLayout drawer;
    private boolean isuserloggedin;
    private TextView header_username,header_email;
    private String profilepic,user_name,email,username,firstname,lastname,password;
    private ImageView header_image;
    private JSONObject jsonObject;
    private int hotel_id;
    private int counter = 0;
    static String city ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);



        NavigationView navigationView= findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hview = navigationView.getHeaderView(0);

        header_image = hview.findViewById(R.id.header_image);
        header_username = hview.findViewById(R.id.header_username);
        header_email = hview.findViewById(R.id.header_emailaddress);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);
        username = sharedPreferences.getString("username", "");



        if(!isConnected(DashBoardActivity.this)){
            buildDialog2(DashBoardActivity.this).show();
        }else{


                VolleyRequest();



        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Profile()).commit();
                break;
            case R.id.nav_booking:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookingFragment()).commit();
                break;
            case R.id.nav_favourite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Favourites()).commit();
                break;
            case R.id.nav_logout:
                buildDialog(DashBoardActivity.this,"Are you sure","You will be logged out of the system").show();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
           if(isuserloggedin){
                counter+=1;
               if(counter<2){
                   Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

               }else{
                  finishAffinity();
               }
               }else{
               super.onBackPressed();
           }
        }

    }



    public void VolleyRequest() {
            final RequestQueue queue = Volley.newRequestQueue(DashBoardActivity.this);
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = Urls.SEARCH_USERS;
            Log.d("112233","Url Setting");
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("112233","Url Setting");
                            try {
                                JSONObject reader = new JSONObject(response);
                                String status = reader.getString("status");

                                if (status.equals("500")) {
                                    buildDialog(DashBoardActivity.this, "500", "Internal Server Error").show();
                                }else if(status.equals("404")){
                                    Toast.makeText(DashBoardActivity.this,"No Record was found",Toast.LENGTH_LONG).show();


                                } else if (status.equals("200")) {
                                    //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                    JSONArray collections = reader.getJSONArray("response");

                                    Log.i("Dashboard Activity", String.valueOf(reader));
                                    for (int i = 0; i < collections.length(); i++) {
                                        jsonObject = collections.getJSONObject(i);
                                        user_name = jsonObject.getString("username");
                                        email = jsonObject.getString("email");
                                        profilepic = jsonObject.getString("profilepic");
                                        firstname = jsonObject.getString("firstname");
                                        lastname  =jsonObject.getString("lastname");
                                        password = jsonObject.getString("password");
                                        UserInfoModel userInfoModel = new UserInfoModel(firstname,lastname,username,email,profilepic);
                                    }
                                    header_email.setText(email);
                                    header_username.setText(user_name);
                                    Glide.with(DashBoardActivity.this).load(Urls.DOMAIN+"/assets/profilepictures/"+profilepic).
                                            circleCrop().
                                            into(header_image);



                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Collection Fragment", error.toString());
                    error.printStackTrace();
                    buildDialog3(DashBoardActivity.this, "Oops..!", "Error occured").show();
                }
            }) {
                //adding parameters to the request
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
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
    public AlertDialog.Builder buildDialog3(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();
        return builder;
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






  public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isuserloggedin",false);
                editor.putString("username",null);

                editor.apply();
              Intent intent = new Intent(DashBoardActivity.this,MainActivity.class);
              startActivity(intent);
              finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
}