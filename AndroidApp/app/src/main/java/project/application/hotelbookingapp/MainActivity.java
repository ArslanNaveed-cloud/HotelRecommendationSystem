package project.application.hotelbookingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    LocationManager locationManager;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private Button btn_signup,btn_login;
    private String username;
    private double LAT;
    private double LON;
    Boolean isuserloggedin=false;


    @Override
    protected void onRestart() {
        checklogin();
        super.onRestart();

    }

    private void checklogin() {
        if(isuserloggedin){
            finish();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo",Context.MODE_PRIVATE);

        isuserloggedin =  sharedPreferences.getBoolean("isuserloggedin",false);

        setContentView(R.layout.activity_main);
        if(isuserloggedin) {
            Intent intent = new Intent(MainActivity.this,DashBoardActivity.class);
            startActivity(intent);
            finish();
        }
        btn_signup = findViewById(R.id.signup_btn);
        btn_login = findViewById(R.id.login_btn);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                    startActivity(intent);

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);

            }
        });



    }

}