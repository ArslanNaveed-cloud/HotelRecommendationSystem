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
import android.widget.Button;
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

public class RoomDetail extends AppCompatActivity {

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
    private RatingBar ratingBar;
    float ratingvalue;
    private TextInputEditText tname;
    private String myname="";
    RadioButton radioButton;
    RadioGroup radiogroup;
    private ProgressDialog dialog;
    ArrayList<ReviewsDataModel> reviewsDataModels;
    int myhotel ;
    String myexperience ;
    double myrating;
    String mynames;

    TextView name,description,price,discount;
    String rname,rdescription,rcoverimage,roomgalley_images,username,experience,rating;
    ImageView coverimage;
    double rprice,rdiscount;
    int room_id,hotel_id;
    RecyclerView roomgalley_imageslist;
    LinearLayout reviews,givefeedback;
    RecyclerView recyclerView;
    Button booknow,addtofavourite,submitrating;
    private int myroomid;
    private TextView myreviews;
    LinearLayout ratingwrapper;
    TextView rating1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radiogroup = findViewById(R.id.message);
        name = findViewById(R.id.my_title);
        description = findViewById(R.id.my_description);
        price = findViewById(R.id.price);
        discount = findViewById(R.id.discount);
        coverimage = findViewById(R.id.imagecontainer);
        recyclerView = findViewById(R.id.roomgalley_imageslist);
        givefeedback = findViewById(R.id.givefeedback);
        givefeedback.setVisibility(View.GONE);
        submitrating = findViewById(R.id.submitrating);
        ratingwrapper = findViewById(R.id.ratingwrapper);
        ratingwrapper.setVisibility(View.GONE);
        rating1 = findViewById(R.id.rating);

        submitrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContents();
            }
        });
        reviewsDataModels = new ArrayList<>();
        myreviews = findViewById(R.id.myreviews);
        ratingBar = findViewById(R.id.ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingvalue =rating;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        booknow = findViewById(R.id.bookroom);
        addtofavourite = findViewById(R.id.addtofavourite);
        GetReviews();

        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomDetail.this,CheckoutActivityJava.class);
                intent.putExtra("roomname",rname);
                intent.putExtra("roomid",room_id);
                intent.putExtra("hotelid",hotel_id);
                intent.putExtra("rprice",rprice);
                intent.putExtra("discount",rdiscount);
                intent.putExtra("username",username);

                startActivity(intent);
            }
        });
        addtofavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(RoomDetail.this, "Please Wait",
                        "Adding room information..");
                dialog.show();
                AddToFavurite();
            }
        });
        Intent intent = getIntent();

        if(intent!=null){
            rname = intent.getStringExtra("name");
            rdescription = intent.getStringExtra("description");
            rprice = intent.getIntExtra("price",0);
            rdiscount = intent.getIntExtra("discount",0);
            rcoverimage = intent.getStringExtra("coverimage");
            hotel_id = intent.getIntExtra("hotel_id",0);
            room_id = intent.getIntExtra("id",0);
            roomgalley_images = intent.getStringExtra("galleryimage");
            if(intent.getStringExtra("rating")!=null){
                rating = intent.getStringExtra("rating");
                ratingwrapper.setVisibility(View.VISIBLE);
                rating1.setText(rating);
            }

        }




        Toast.makeText(this, ""+rprice+""+rdiscount, Toast.LENGTH_SHORT).show();
        name.setText(rname);
        description.setText(rdescription);
        price.setText(String.valueOf(rprice)+" PKR/ pernight");
        discount.setText(String.valueOf(rdiscount));
        Glide.with(this).load(Urls.DOMAIN+"/assets/roomimages/"+rcoverimage).
                into(coverimage);
        try {
            JSONArray galleryImages = new JSONArray(roomgalley_images);
            Gallery_Adaphter adaphter = new Gallery_Adaphter(galleryImages,"room");
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
    private void GetReviews()
    {
        final RequestQueue queue = Volley.newRequestQueue(RoomDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_ROOMREVIEWS;
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
                                JSONArray id = reader.getJSONArray("room_id");
                                JSONArray experience = reader.getJSONArray("experience");
                                JSONArray rating = reader.getJSONArray("rating");
                                JSONArray names = reader.getJSONArray("names");

                                for(int i = 0;i<id.length();i++){
                                    myroomid = id.getInt(i);
                                    myexperience = experience.getString(i);
                                    myrating = rating.getDouble(i);
                                    mynames = names.getString(i);
                                    ReviewsDataModel dataModel = new ReviewsDataModel(myroomid,myexperience,myrating,mynames);
                                    reviewsDataModels.add(dataModel);

                                }
                                RecyclerView recyclerView = findViewById(R.id.commentslist);
                                reviewadaphter recycleradaphter = new reviewadaphter(reviewsDataModels);
                                recyclerView.setLayoutManager(new LinearLayoutManager(RoomDetail.this));
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
                buildDialog(RoomDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("roomid", String.valueOf(room_id));

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

    private void checkContents() {

        int radiobuttonid = radiogroup.getCheckedRadioButtonId();
        radioButton= findViewById(radiobuttonid);
        experience = radioButton.getText().toString().trim();
        if(experience ==null || experience.isEmpty()){
            Toast.makeText(this, "Please select experience.", Toast.LENGTH_SHORT).show();
        }else if(ratingvalue ==0){
            Toast.makeText(this, "Please give rating", Toast.LENGTH_SHORT).show();

        }else{
            dialog = ProgressDialog.show(RoomDetail.this, "Please Wait",
                    "submitting review..");
            dialog.show();
            InsertReviews();
        }
    }
    private void InsertReviews() {
        final RequestQueue queue = Volley.newRequestQueue(RoomDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.INSERT_RoomREVIEWS;
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

                                buildDialog(RoomDetail.this, "500", "Internal Server Error").show();
                                dialog.dismiss();

                            }else if(status.equals("404")){
                                Toast.makeText(RoomDetail.this, "Review not added\nPlease try again later", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else if (status.equals("200")) {
                                Toast.makeText(RoomDetail.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

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
                buildDialog(RoomDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("roomid", String.valueOf(room_id));
                params.put("rating", String.valueOf(ratingvalue));
                params.put("experience", experience);

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

    private void AddToFavurite() {
        final RequestQueue queue = Volley.newRequestQueue(RoomDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.ADDTOFAVOURITES;
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

                                buildDialog(RoomDetail.this, "500", "Internal Server Error").show();
                                dialog.dismiss();

                            }else if(status.equals("404")){
                                Toast.makeText(RoomDetail.this, "Room not added\nPlease try again later", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else if (status.equals("200")) {
                                Toast.makeText(RoomDetail.this, "Room added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

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
                buildDialog(RoomDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("roomid", String.valueOf(room_id));
                params.put("roomname",rname);
                params.put("coverimage",rcoverimage);
                params.put("galleryimages",roomgalley_images);
                params.put("hotelid", String.valueOf(hotel_id));
                params.put("username", username);
                params.put("description", rdescription);
                params.put("price", String.valueOf(rprice));
                params.put("discount", String.valueOf(rdiscount));


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
        final RequestQueue queue = Volley.newRequestQueue(RoomDetail.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_ROOMBOOKING;
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

                                buildDialog(RoomDetail.this, "500", "Internal Server Error").show();
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
                buildDialog(RoomDetail.this, "Oops..!", "Please try again later").show();
            }

        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("roomid", String.valueOf(room_id));

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