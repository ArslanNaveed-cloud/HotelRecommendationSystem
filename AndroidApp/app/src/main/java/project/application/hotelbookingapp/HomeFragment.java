package project.application.hotelbookingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback,LocationListener{

    private ArrayList<HotelDataModel> arrayList;
    private ArrayList<RecommendationDataModel> recommendationDataModelArrayList;
    private ArrayList<HighestRatedHotelDataModel> ratedHotelDataModels;
    private ArrayList<HotelRatingDataModel> arrayList2;
    private ArrayList<PopularHotelDataModel> popularHotelDataModels;
    LocationManager locationManager;
    private LinearLayout loader;
    private String city;
    public Integer[] city_images={R.drawable.ic_location,R.drawable.isl,R.drawable.karachi,R.drawable.kashmir,R.drawable.lahore,R.drawable.multan,R.drawable.murree,R.drawable.naran,R.drawable.nathiagali,R.drawable.peshawar,R.drawable.rawalpindi};
    public String[] city_names = {"NearBy","Islamabad","Karachi","kashmir","Lahore","Multan","Murree","Naran","Nathiagali","Peshawar","Rawalpindi"};
    private String hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,hotel_galleryimages;
    private int hotel_id;
    cityrvadaphter cityrvadaphter;
    HighestRatedAdaphter highestRatedAdaphter;
    popularadaphter popularadaphter;
    hotelrvadaphter hotelrvadaphter;
    RecommendationAdapahter recommendationAdapahter;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    private TextView ratingheading,popularheading,recomendationheading;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        popularHotelDataModels = new ArrayList<>();
        arrayList2 =new ArrayList<>();
        recommendationDataModelArrayList = new ArrayList<>();
        if(getArguments()!=null){
            city = getArguments().getString("cityname");
            //Toast.makeText(getActivity(), "city = "+city, Toast.LENGTH_SHORT).show();
            GetHotelratings();

        }else{
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }else{
                getLocation();

            }
        }
        ratedHotelDataModels = new ArrayList<>();


        view =  inflater.inflate(R.layout.fragment_home, container, false);
        popularheading = view.findViewById(R.id.popularheading);
        recomendationheading = view.findViewById(R.id.recomendationheading);
        recomendationheading.setVisibility(View.GONE);
        popularheading.setVisibility(View.GONE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ratingheading = view.findViewById(R.id.ratingheading);
        ratingheading.setVisibility(View.GONE);
        loader = view.findViewById(R.id.loader);
        arrayList = new ArrayList<>();

        return view;
    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);



                city = addresses.get(0).getLocality();
                GetHotelratings();




        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void GetHotelratings() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_HOTELRATING;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233","Url Setting"+response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                GetHotelDetails(city);

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray hotel_id = reader.getJSONArray("hotel_id");
                                JSONArray hotel_rating = reader.getJSONArray("hotel_rating");
                                arrayList2.clear();

                                for(int i = 0;i < hotel_id.length();i++){
                                    int id = hotel_id.getInt(i);
                                    float rating = (float) hotel_rating.getDouble(i);
                                    HotelRatingDataModel ratingDataModel = new HotelRatingDataModel(id,rating);
                                    arrayList2.add(ratingDataModel);
                                }

                                GetHotelDetails(city);


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
                loader.setVisibility(View.GONE);
                buildDialog3(getContext(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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

    private void GetHotelDetails(final String city) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_HOTEL;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233","Url Setting"+response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(getActivity(),"No Record was found",Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                JSONArray id = reader.getJSONArray("id");
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray address = reader.getJSONArray("address");
                                JSONArray city = reader.getJSONArray("city");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");

                                JSONArray galleryimages = reader.getJSONArray("galleryimages");
                                JSONArray my_gallery;
                                JSONArray galleryarray;
                                arrayList.clear();


                                for(int i = 0 ;i < id.length();i++){
                                    hotel_id = id.getInt(i);
                                    hotel_name = title.getString(i);
                                    hotel_address = address.getString(i);
                                    hotel_city = city.getString(i);
                                    hotel_description = description.getString(i);
                                    hotel_coverimage = coverimage.getString(i);
                                    hotel_galleryimages = galleryimages.getString(i);
                                    my_gallery = new JSONArray(hotel_galleryimages);
                                    Log.d("Gallery Images = ",""+my_gallery);

                                    HotelDataModel dataModel = new HotelDataModel(hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,hotel_id,my_gallery);
                                    arrayList.add(dataModel);


                                }
                                cityrvadaphter = new cityrvadaphter(city_images,city_names);
                                hotelrvadaphter = new hotelrvadaphter(arrayList,arrayList2);


                                RecyclerView recyclerView = view.findViewById(R.id.cityrv);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
                                recyclerView.setAdapter(cityrvadaphter);

                                RecyclerView recyclerView2 = view.findViewById(R.id.hotelrv);
                                recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
                                recyclerView2.setAdapter(hotelrvadaphter);

                                GetHighestRated();
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
                loader.setVisibility(View.GONE);
                buildDialog3(getContext(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
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
   private void GetHighestRated() {

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_HIGHESTRATEDHOTEL;
        Log.d("Getting Highest Rated","Get Highest Rated");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Getting Highest Rated",response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                              GetPopular();
                            } else if (status.equals("200")) {

                                JSONArray id = reader.getJSONArray("id");
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray address = reader.getJSONArray("address");
                                JSONArray city = reader.getJSONArray("city");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");

                                JSONArray galleryimages = reader.getJSONArray("galleryimages");
                                JSONArray my_gallery;
                                JSONArray galleryarray;
                                ratedHotelDataModels.clear();

                                for(int i = 0 ;i < id.length();i++){
                                    hotel_id = id.getInt(i);
                                    hotel_name = title.getString(i);
                                    hotel_address = address.getString(i);
                                    hotel_city = city.getString(i);
                                    hotel_description = description.getString(i);
                                    hotel_coverimage = coverimage.getString(i);
                                    hotel_galleryimages = galleryimages.getString(i);
                                    my_gallery = new JSONArray(hotel_galleryimages);
                                    Log.d("Getting HighestRated",""+my_gallery);

                                    HighestRatedHotelDataModel dataModel = new HighestRatedHotelDataModel(hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,hotel_id,my_gallery);
                                    ratedHotelDataModels.add(dataModel);

                                }
                                highestRatedAdaphter = new HighestRatedAdaphter(ratedHotelDataModels,arrayList2);
                                RecyclerView recyclerView3 = view.findViewById(R.id.ratedhotelrv);
                                recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
                                recyclerView3.setAdapter(highestRatedAdaphter);
                                ratingheading.setVisibility(View.VISIBLE);


                                GetPopular();


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
                buildDialog3(getContext(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
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
    private void GetPopular() {

        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_POPULARHOTEL;
        Log.d("Getting Highest Rated","Get Highest Rated");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Getting Highest Rated",response);
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                GetRecommendations();

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                JSONArray id = reader.getJSONArray("id");
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray address = reader.getJSONArray("address");
                                JSONArray city = reader.getJSONArray("city");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");

                                JSONArray galleryimages = reader.getJSONArray("galleryimages");
                                JSONArray my_gallery;
                                JSONArray galleryarray;
                                popularHotelDataModels.clear();


                                for(int i = 0 ;i < id.length();i++){
                                    hotel_id = id.getInt(i);
                                    hotel_name = title.getString(i);
                                    hotel_address = address.getString(i);
                                    hotel_city = city.getString(i);
                                    hotel_description = description.getString(i);
                                    hotel_coverimage = coverimage.getString(i);
                                    hotel_galleryimages = galleryimages.getString(i);
                                    my_gallery = new JSONArray(hotel_galleryimages);
                                    Log.d("Getting HighestRated",""+my_gallery);

                                    PopularHotelDataModel dataModel = new PopularHotelDataModel(hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,hotel_id,my_gallery);
                                    popularHotelDataModels.add(dataModel);


                                }
                               popularadaphter = new popularadaphter(popularHotelDataModels,arrayList2);
                                RecyclerView recyclerView4 = view.findViewById(R.id.popularhotelrv);
                                recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
                                recyclerView4.setAdapter(popularadaphter);
                                popularheading.setVisibility(View.VISIBLE);
                                GetRecommendations();

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
                buildDialog3(getContext(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
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
    private void GetRecommendations() {
        Log.d("Recommendations", "GetRecommendations: ");
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GETRECOMMENDATIONS;
        Log.d("112233","Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                          Log.d("Response", "GetRecommendations: "+response);

                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                buildDialog3(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                               loader.setVisibility(View.GONE);
                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray id = reader.getJSONArray("id");
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray address = reader.getJSONArray("address");
                                JSONArray city = reader.getJSONArray("city");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");

                                JSONArray galleryimages = reader.getJSONArray("galleryimages");
                                JSONArray my_gallery;
                                JSONArray galleryarray;
                                recommendationDataModelArrayList.clear();
                                for(int i = 0 ;i < id.length();i++){
                                    hotel_id = id.getInt(i);
                                    hotel_name = title.getString(i);
                                    hotel_address = address.getString(i);
                                    hotel_city = city.getString(i);
                                    hotel_description = description.getString(i);
                                    hotel_coverimage = coverimage.getString(i);
                                    hotel_galleryimages = galleryimages.getString(i);
                                    my_gallery = new JSONArray(hotel_galleryimages);
                                    Log.d("Gallery Images = ",""+my_gallery);

                                    RecommendationDataModel dataModel = new RecommendationDataModel(hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,hotel_id,my_gallery);
                                    recommendationDataModelArrayList.add(dataModel);


                                }
                                recommendationAdapahter = new RecommendationAdapahter(recommendationDataModelArrayList,arrayList2);

                                RecyclerView recyclerView4 = view.findViewById(R.id.recommendationrv);
                                recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
                                recyclerView4.setAdapter(recommendationAdapahter);
                                recomendationheading.setVisibility(View.VISIBLE);
                                loader.setVisibility(View.GONE);

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
                loader.setVisibility(View.GONE);
                buildDialog3(getContext(), "Oops..!", "Please try again later").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("city", city);
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

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo",Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isuserloggedin",false);
                editor.putString("username",null);

                editor.apply();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
    public AlertDialog.Builder buildDialog3(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();
        return builder;
    }
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                getActivity().finish();
            }
        }, 2000);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // Toast.makeText(this, "Gps Already Enabled", Toast.LENGTH_SHORT).show();
                getLocation();

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {

                //Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                //locationEnabled();
                getLocation();
            } else {
                Toast.makeText(getActivity(), "You need to enable gps to use this application", Toast.LENGTH_LONG).show();

                waitforsometime();
            }

        }
    }

}