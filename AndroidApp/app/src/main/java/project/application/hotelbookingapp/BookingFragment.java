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
import androidx.recyclerview.widget.LinearLayoutManager;
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


public class BookingFragment extends Fragment {

    View view;
    public boolean isactionmode=false;
    bookingrvadaphter bookingrvadaphter;
    private String roomname,hotelname,startdate,enddate,stay,coveriamge,username;
    private int room_price;
    private LinearLayout loader;
    private ArrayList<BookingDataModel> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_booking, container, false);
            loader = view.findViewById(R.id.loader);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        arrayList = new ArrayList<>();
        setHasOptionsMenu(true);
        GetRoomDetails();
        return view;
    }

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
                        if(bookingrvadaphter == null){
                            Toast.makeText(getActivity(), "Please add data to apply search", Toast.LENGTH_SHORT).show();
                        }else {
                            bookingrvadaphter.getFilter().filter(newText);
                        }
                        return true;
                    }
                });
                break;
            }


        return true;
    }

    private void GetRoomDetails() {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.GET_BOOKINGLIST;
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
                                waitforsometime();

                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();

                                JSONArray id = reader.getJSONArray("id");
                                JSONArray price = reader.getJSONArray("price");
                                JSONArray start_date = reader.getJSONArray("startdate");
                                JSONArray end_date = reader.getJSONArray("enddate");
                                JSONArray total_stay = reader.getJSONArray("stay");
                                JSONArray roomimage = reader.getJSONArray("roomimage");
                                JSONArray room_name = reader.getJSONArray("roomname");
                                JSONArray hotel_name = reader.getJSONArray("hotelname");

                                for(int i = 0 ;i < id.length();i++){
                                     roomname = room_name.getString(i);
                                    room_price = price.getInt(i);
                                    startdate = String.valueOf(start_date.get(i));
                                    enddate = String.valueOf(end_date.get(i));
                                    stay = total_stay.getString(i);
                                    coveriamge = roomimage.getString(i);
                                    hotelname = hotel_name.getString(i);

                                    BookingDataModel dataModel = new BookingDataModel(roomname,hotelname,startdate,enddate,stay,coveriamge,room_price);
                                    arrayList.add(dataModel);

                                }
                                bookingrvadaphter = new bookingrvadaphter(arrayList);
                                RecyclerView recyclerView = view.findViewById(R.id.bookinglist);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(bookingrvadaphter);

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
                buildDialog(getActivity(), "Oops..!", "Please try again later").show();
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

            }
        }, 2000);
    }
}