package project.application.hotelbookingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Favourites extends Fragment {
    View view;
    private int id;
    private String room_name,room_description,room_coverimage,room_galleryimages;
    private int room_id,hotel_id,room_price,room_discount;
    private roomrvadaphter roomrvadaphter;
    private LinearLayout loader;
    private ArrayList<RoomDataModel> arrayList;

    private ArrayList<RoomRatingDataModel> arrayList2;
    private String username;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.custommenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_search:
                androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(roomrvadaphter == null){
                            Toast.makeText(getActivity(), "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            roomrvadaphter.getFilter().filter(newText);
                        }
                        return true;
                    }
                });
                break;
        }


        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourites, container, false);
        loader = view.findViewById(R.id.loader);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        GetFavourites();
        return view;
    }
    private void GetRoomRatings() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_ROOMRATING;
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
                                GetFavourites();

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                JSONArray room_id = reader.getJSONArray("room_id");
                                JSONArray room_rating = reader.getJSONArray("room_rating");
                                arrayList2.clear();

                                for(int i = 0;i < room_id.length();i++){
                                    int id = room_id.getInt(i);
                                    float rating = (float) room_rating.getDouble(i);
                                    RoomRatingDataModel ratingDataModel = new RoomRatingDataModel(id,rating);
                                    arrayList2.add(ratingDataModel);
                                }

                                GetFavourites();


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
                buildDialog3(getActivity(), "Oops..!", "Please try again later").show();
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

    private void GetFavourites() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GETFAVOURITES;
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
                                buildDialog(getActivity(), "500", "Internal Server Error").show();
                            }else if(status.equals("404")){
                                Toast.makeText(getActivity(),"No Record was found",Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new HomeFragment()).commit();

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                JSONArray id = reader.getJSONArray("id");
                                JSONArray title = reader.getJSONArray("title");
                                JSONArray price = reader.getJSONArray("price");
                                JSONArray discount = reader.getJSONArray("discount");
                                JSONArray hotelid = reader.getJSONArray("hotel_id");
                                JSONArray description = reader.getJSONArray("description");
                                JSONArray coverimage = reader.getJSONArray("coverimage");

                                JSONArray galleryimages = reader.getJSONArray("galleryimages");
                                JSONArray my_gallery;
                                JSONArray galleryarray;
                                arrayList.clear();


                                for(int i = 0 ;i < id.length();i++){
                                    room_id = id.getInt(i);
                                    room_name = title.getString(i);
                                    room_price = price.getInt(i);
                                    room_discount = discount.getInt(i);
                                    hotel_id = hotelid.getInt(i);
                                    room_description = description.getString(i);
                                    room_coverimage = coverimage.getString(i);
                                    room_galleryimages = galleryimages.getString(i);
                                    my_gallery = new JSONArray(room_galleryimages);
                                    Log.d("Gallery Images = ",""+my_gallery);

                                    RoomDataModel dataModel = new RoomDataModel(room_name,room_description,room_coverimage,hotel_id,room_id,room_price,room_discount,my_gallery);
                                    arrayList.add(dataModel);
                                    roomrvadaphter = new roomrvadaphter(arrayList,arrayList2);


                                    RecyclerView recyclerView = view.findViewById(R.id.favouriterecycle);
                                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
                                    recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
                                    recyclerView.setAdapter(roomrvadaphter);

                                    loader.setVisibility(View.GONE);

                                }

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
                buildDialog3(getActivity(), "Oops..!", "Please try again later").show();
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
    public void waitforsometime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                getActivity().finish();
            }
        }, 2000);
    }
    public AlertDialog.Builder buildDialog(Context c, String header, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);
        waitforsometime();
        return builder;
    }

}