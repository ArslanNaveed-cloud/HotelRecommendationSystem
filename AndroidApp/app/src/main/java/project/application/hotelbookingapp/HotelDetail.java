package project.application.hotelbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
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
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HotelDetail extends AppCompatActivity {
    private String hotel_name,hotel_address,hotel_city,hotel_description,hotel_coverimage,rating,hotel_galleryimages,username,experience;
    private int hotel_id;
    ImageView coverimage;
    TextView name,address,city,description;
    LinearLayout givefeedback;
    RecyclerView recyclerView;
    private Button showroombtn,submitrating;
    private RatingBar ratingBar;
    float ratingvalue;
    private String myname="";
    RadioButton radioButton;
    RadioGroup radiogroup;
    private ProgressDialog dialog;
    private TextView myreviews;
    ArrayList<ReviewsDataModel> reviewsDataModels;
    int myhotelid ;
    String myexperience ;
    double myrating;
    String mynames;
    LinearLayout ratingwrapper;
    TextView rating1;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        reviewsDataModels = new ArrayList<>();
        setContentView(R.layout.activity_hotel_detail);
        ratingwrapper = findViewById(R.id.ratingwrapper);
        ratingwrapper.setVisibility(View.GONE);
        rating1 = findViewById(R.id.rating);

        radiogroup = findViewById(R.id.message);
        submitrating = findViewById(R.id.submitrating);
        submitrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContents();
            }
        });


        ratingBar = findViewById(R.id.ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingvalue =rating;
            }
        });
        GetReviews();
        myreviews = findViewById(R.id.myreviews);
        myreviews.setVisibility(View.GONE);
        showroombtn = findViewById(R.id.showroombtn);
        showroombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelDetail.this,ShowRoom.class);
                intent.putExtra("hotel_id",hotel_id);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        if(intent!=null){
            hotel_name = intent.getStringExtra("name");
            hotel_address = intent.getStringExtra("address");
            hotel_city = intent.getStringExtra("city");
            hotel_description = intent.getStringExtra("description");
            hotel_coverimage = intent.getStringExtra("coverimage");
            hotel_galleryimages = intent.getStringExtra("galleryimage");
            hotel_id = intent.getIntExtra("id",0);
            if(intent.getStringExtra("rating")!=null){
                rating = intent.getStringExtra("rating");
                ratingwrapper.setVisibility(View.VISIBLE);
                rating1.setText(rating);
            }
        }
        recyclerView = findViewById(R.id.galley_imageslist);
        coverimage = findViewById(R.id.imagecontainer);
        name = findViewById(R.id.my_title);
        city = findViewById(R.id.my_city);
        address = findViewById(R.id.my_address);
        description = findViewById(R.id.my_description);
        coverimage = findViewById(R.id.imagecontainer);
        givefeedback = findViewById(R.id.givefeedback);
        Glide.with(this).load(Urls.DOMAIN+"/assets/hotelimages/"+hotel_coverimage).
                into(coverimage);
        name.setText(hotel_name);
        address.setText(hotel_address);
        city.setText(hotel_city);
        description.setText(hotel_description);
        try {
            JSONArray galleryImages = new JSONArray(hotel_galleryimages);
            Gallery_Adaphter adaphter = new Gallery_Adaphter(galleryImages,"");
            // set a GridLayoutManager with default vertical orientation and 2 number of columns
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
            recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            //  call the constructor of CustomAdapter to send the reference and data to Adapter

            recyclerView.setAdapter(adaphter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        CheckForBookings();

    }

    private void checkContents() {

        int radiobuttonid = radiogroup.getCheckedRadioButtonId();
        radioButton= findViewById(radiobuttonid);
        experience = radioButton.getText().toString().trim();
        if(experience ==null || experience.isEmpty()){
            Toast.makeText(this, "Please select experience.", Toast.LENGTH_SHORT).show();
        }else if(ratingvalue ==0){
            Toast.makeText(this, "Please give rating", Toast.LENGTH_SHORT).show();

        }else{
            dialog = ProgressDialog.show(HotelDetail.this, "Please Wait",
                    "submitting review..");
            dialog.show();
            InsertReviews();
        }
    }

    private void InsertReviews() {
        final RequestQueue queue = Volley.newRequestQueue(HotelDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.INSERT_REVIEWS;
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
                                givefeedback.setVisibility(View.GONE);

                                buildDialog(HotelDetail.this, "500", "Internal Server Error").show();
                                dialog.dismiss();

                            }else if(status.equals("404")){
                                Toast.makeText(HotelDetail.this, "Review not added\nPlease try again later", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else if (status.equals("200")) {
                                Toast.makeText(HotelDetail.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                Intent intent = new Intent(HotelDetail.this,DashBoardActivity.class);
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
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                givefeedback.setVisibility(View.GONE);
                buildDialog(HotelDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("hotelid", String.valueOf(hotel_id));
                params.put("rating", String.valueOf(ratingvalue));
                params.put("experience", experience);
                params.put("hotelname",hotel_name);


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
    private void GetReviews()
    {
        final RequestQueue queue = Volley.newRequestQueue(HotelDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_REVIEWS;
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
                                myreviews.setVisibility(View.GONE);
                                //buildDialog(HotelDetail.this, "500", "Internal Server Error").show();

                            }else if(status.equals("404")){
                                myreviews.setVisibility(View.GONE);
                               // Toast.makeText(HotelDetail.this, "Review not added\nPlease try again later", Toast.LENGTH_SHORT).show();

                            } else if (status.equals("200")) {
                                reviewsDataModels.clear();
                                JSONArray id = reader.getJSONArray("hotel_id");
                                JSONArray experience = reader.getJSONArray("experience");
                                JSONArray rating = reader.getJSONArray("rating");
                                JSONArray names = reader.getJSONArray("names");

                                for(int i = 0;i<id.length();i++){
                                    myhotelid = id.getInt(i);
                                    myexperience = experience.getString(i);
                                     myrating = rating.getDouble(i);
                                    mynames = names.getString(i);
                                    ReviewsDataModel dataModel = new ReviewsDataModel(myhotelid,myexperience,myrating,mynames);
                                    reviewsDataModels.add(dataModel);

                                }
                                RecyclerView recyclerView = findViewById(R.id.commentslist);
                                reviewadaphter recycleradaphter = new reviewadaphter(reviewsDataModels);
                                recyclerView.setLayoutManager(new LinearLayoutManager(HotelDetail.this));
                                recyclerView.setAdapter(recycleradaphter);





                                myreviews.setVisibility(View.VISIBLE);

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
                myreviews.setVisibility(View.VISIBLE);
                buildDialog(HotelDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hotelid", String.valueOf(hotel_id));

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

    private void CheckForBookings() {
        final RequestQueue queue = Volley.newRequestQueue(HotelDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_BOOKING;
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
                                givefeedback.setVisibility(View.GONE);

                                buildDialog(HotelDetail.this, "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                givefeedback.setVisibility(View.GONE);


                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                givefeedback.setVisibility(View.VISIBLE);




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
                givefeedback.setVisibility(View.GONE);
                buildDialog(HotelDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                 params.put("username", username);
                params.put("hotelid", String.valueOf(hotel_id));

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
}