package project.application.hotelbookingapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;

import com.stripe.android.ApiResultCallback;

import com.stripe.android.PaymentIntentResult;

import com.stripe.android.Stripe;

import com.stripe.android.model.ConfirmPaymentIntentParams;

import com.stripe.android.model.PaymentIntent;

import com.stripe.android.model.PaymentMethodCreateParams;

import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.lang.ref.WeakReference;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;

import okhttp3.Call;

import okhttp3.Callback;

import okhttp3.MediaType;

import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.RequestBody;

import okhttp3.Response;
import project.application.hotelbookingapp.R;
import project.application.hotelbookingapp.Urls;

public class CheckoutActivityJava extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost


    private OkHttpClient httpClient = new OkHttpClient();
    CheckoutActivityJava obj;
    private String paymentIntentClientSecret;
    String rname, rdescription, rcoverimage, roomgalley_images, username;
    static boolean isdatafound, enableenddate;
    double amount;
    int myday;
    private Stripe stripe;
    int room_id, hotel_id;
    double rprice, rdiscount;
    private EditText startdate, enddate;
    private Button cashonarrival;
    private String startdate1, enddate1 = "";
    private String days;
    private String mystartdate, myenddate = "";
    private Button payButton;
    private ProgressDialog dialog;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String ispayment;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.checkoutactivity);
        enableenddate = false;
        obj = new CheckoutActivityJava();
        Intent intent = getIntent();
        rname = intent.getStringExtra("roomname");
        room_id = intent.getIntExtra("roomid", 0);
        hotel_id = intent.getIntExtra("hotelid", 0);
        rprice = intent.getDoubleExtra("rprice", 0);
        rdiscount = intent.getDoubleExtra("discount", 0);
        username = intent.getStringExtra("username");
        payButton = findViewById(R.id.payButton);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        cashonarrival = findViewById(R.id.cashonarrival);
        cashonarrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkcontent();
            }
        });
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartdate();
            }
        });


        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableenddate) {
                    setEnddate();
                } else {
                    Toast.makeText(CheckoutActivityJava.this, "Please select a start date first", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // Configure the SDK with your Stripe publishable key so it can make requests to Stripe

        stripe = new Stripe(
                getApplicationContext(),
                "pk_test_51HdY2mJ0pWGmfZTV4Tw8LEDm0dm9UVjTgsaogvMjN303iWVAffIPYeiE7QmzNbTS0z9AUoRu4l15da1ZAPTg1mz300viAqHBrp"
        );
        payButton.setOnClickListener((View view) -> {

            Log.i("stripe", "payButton.setOnClickListener");

            if (startdate1 == null || startdate1.isEmpty()) {
                Toast.makeText(this, "Please Select a start date", Toast.LENGTH_SHORT).show();
            } else if (enddate1 == null || enddate1.isEmpty()) {
                Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
            } else {

                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);

                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

                if (params != null) {
                    dialog = ProgressDialog.show(CheckoutActivityJava.this, "Please Wait",
                            "Processing your payment...");
                    dialog.show();
                    ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams

                            .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);

                    stripe.confirmPayment(this, confirmParams);

                }
            }

        });


    }

    private void checkcontent() {
        if (startdate1 == null || startdate1.isEmpty()) {
            Toast.makeText(this, "Please Select a start date", Toast.LENGTH_SHORT).show();
        } else if (enddate1 == null || enddate1.isEmpty()) {
            Toast.makeText(this, "Please select an end date", Toast.LENGTH_SHORT).show();
        }else{
            dialog = ProgressDialog.show(CheckoutActivityJava.this, "Please Wait",
                    "Processing your payment...");
            dialog.show();
            ispayment="no";
            InsertBookingRecord();
        }
    }

    public void InsertBookingRecord() {
        final RequestQueue queue = Volley.newRequestQueue(CheckoutActivityJava.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.INSERT_BOOKINGS;
        Log.d("112233", "Insert Booking Record");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233", "Insert Bookings");
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {

                                dialog.dismiss();
                                buildDialog(CheckoutActivityJava.this, "500", "Internal Server Error\nPlease try again later").show();
                            } else if (status.equals("404")) {
                                dialog.dismiss();
                                Toast.makeText(CheckoutActivityJava.this, "There was a problem booking your room", Toast.LENGTH_LONG).show();


                            } else if (status.equals("200")) {
                                //Toast.makeText(FriendActivity.this, "User Found", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                //JSONArray collections = reader.getJSONArray("response");
                                buildDialog(CheckoutActivityJava.this, "Congratulations.!", "Your Room is reserved\n").show();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                buildDialog3(CheckoutActivityJava.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("roomid", String.valueOf(room_id));
                params.put("hotelid", String.valueOf(hotel_id));
                params.put("startdate", startdate1);
                params.put("enddate", enddate1);
                params.put("stay", days);
                params.put("price", String.valueOf(rprice));
                params.put("payment", ispayment);



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

    public android.app.AlertDialog.Builder buildDialog(Context c, String header, String message) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();

        return builder;
    }

    private void setStartdate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startdate1 = year + "-" + (month + 1) + "-" + dayOfMonth;
                //Toast.makeText(CheckoutActivityJava.this, ""+startdate1, Toast.LENGTH_SHORT).show();
                CheckStartDate();

            }
        };
        DatePickerDialog pickerDialog = new DatePickerDialog(
                CheckoutActivityJava.this,
                R.style.datepickerCustom,
                dateSetListener,
                year, month, day
        );
        pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        pickerDialog.show();

    }

    private void CheckStartDate() {
        final RequestQueue queue = Volley.newRequestQueue(CheckoutActivityJava.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_STARTDATE;
        Log.d("112233", "Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233", "Url Setting");
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");
                            Log.d("aabbcc", "" + reader);
                            if (status.equals("500")) {
                                Toast.makeText(CheckoutActivityJava.this, "Please try again later..", Toast.LENGTH_SHORT).show();
                            } else if (status.equals("404")) {
                                startdate.setText(startdate1);
                                enableenddate = true;
                            } else if (status.equals("200")) {
                                mystartdate = reader.getString("startdate");
                                myenddate = reader.getString("enddate");
                                Toast.makeText(CheckoutActivityJava.this, "Room Already Reserved From " + mystartdate + " to " + myenddate, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                buildDialog3(CheckoutActivityJava.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date", startdate1);
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

    private void CheckEndDate() {
        final RequestQueue queue = Volley.newRequestQueue(CheckoutActivityJava.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Urls.CHECK_ENDDATE;
        Log.d("112233", "Url Setting");
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("112233", "Url Setting");
                        try {
                            JSONObject reader = new JSONObject(response);
                            String status = reader.getString("status");

                            if (status.equals("500")) {
                                Toast.makeText(CheckoutActivityJava.this, "Please try again later..", Toast.LENGTH_SHORT).show();
                            } else if (status.equals("404")) {
                                days = reader.getString("days");
                                myday = Integer.parseInt(days);
                                enddate.setText(enddate1);

                                if (myday == 0) {
                                    amount = rprice;
                                    payButton.setText("Pay (PKR " + amount + ")");
                                } else {
                                    amount = (rprice * myday);
                                    payButton.setText("Pay PKR (" + amount + ")");


                                }
                                startCheckout();
                            } else if (status.equals("200")) {
                                Toast.makeText(CheckoutActivityJava.this, "Please select a correct date", Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Collection Fragment", error.toString());
                error.printStackTrace();
                buildDialog3(CheckoutActivityJava.this, "Oops..!", "Error occured").show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date", startdate1);
                params.put("enddate", enddate1);
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

    public android.app.AlertDialog.Builder buildDialog3(Context c, String header, String message) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c);
        builder.setTitle(header);
        builder.setMessage(message);

        waitforsometime();
        return builder;
    }

    public void waitforsometime() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();

            }
        }, 2000);
    }

    private void setEnddate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                enddate1 = year + "-" + (month + 1) + "-" + dayOfMonth;
                CheckEndDate();
            }
        };
        DatePickerDialog pickerDialog = new DatePickerDialog(
                CheckoutActivityJava.this,
                R.style.datepickerCustom,
                dateSetListener,
                year, month, day
        );
        pickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        pickerDialog.show();

    }


    private void startCheckout() {

        // Create a PaymentIntent by calling the server's endpoint.

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        // String json = "{"
        //         + "\"currency\":\"pkr\","
        //         + "\"items\":["
        ////        + "{\"id\":\"photo_subscription\"}"
        //       + "]"
        //       + "}";
        Map<String, Object> payMap = new HashMap<String, Object>();
        Map<String, Object> itemMap = new HashMap<String, Object>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency", "pkr");
        itemMap.put("id", "photo_subscription");
        itemMap.put("amount", amount);
        itemList.add(itemMap);
        payMap.put("items", itemList);
        String json = new Gson().toJson(payMap);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(Urls.PAYEMNT)
                .post(body)
                .build();
        Log.i("stripe", "request enqueue");

        httpClient.newCall(request)

                .enqueue(new PayCallback(this));

        // Hook up the pay button to the card widget and stripe instance


    }

    private void displayAlert(@NonNull String title,

                              @Nullable String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)

                .setTitle(title)

                .setMessage(message);

        builder.setPositiveButton("Ok", null);

        builder.create().show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment

        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));

    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Log.i("stripe", "onPaymentSuccess");
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        Map<String, String> responseMap = gson.fromJson(

                Objects.requireNonNull(response.body()).string(),

                type

        );

        paymentIntentClientSecret = responseMap.get("clientSecret");

        Log.i("stripe", "paymentIntentClientSecret = " + paymentIntentClientSecret);

    }

    private static final class PayCallback implements Callback {

        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PayCallback(@NonNull CheckoutActivityJava activity) {

            activityRef = new WeakReference<>(activity);

        }

        @Override

        public void onFailure(@NonNull Call call, @NonNull final IOException e) {
            Log.i("stripe", "onFailure");
            Log.i("stripe", "onFailure - " + e);
            final CheckoutActivityJava activity = activityRef.get();

            if (activity == null) {

                return;

            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    e.printStackTrace();
                }
            });

        }

        @Override

        public void onResponse(@NonNull Call call, @NonNull final Response response)

                throws IOException {

            Log.i("stripe", "onResponse");

            final CheckoutActivityJava activity = activityRef.get();

            if (activity == null) {

                return;

            }

            if (!response.isSuccessful()) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Response Error", "" + response.toString());
                    }
                });

            } else {

                activity.onPaymentSuccess(response);

            }

        }

    }

    private final class PaymentResultCallback

            implements ApiResultCallback<PaymentIntentResult> {

        @NonNull
        private final WeakReference<CheckoutActivityJava> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivityJava activity) {

            activityRef = new WeakReference<>(activity);

        }

        @Override

        public void onSuccess(@NonNull PaymentIntentResult result) {

            final CheckoutActivityJava activity = activityRef.get();

            if (activity == null) {

                return;

            }

            PaymentIntent paymentIntent = result.getIntent();

            PaymentIntent.Status status = paymentIntent.getStatus();

            if (status == PaymentIntent.Status.Succeeded) {

                // Payment completed successfully
                dialog = ProgressDialog.show(CheckoutActivityJava.this, "Please Wait",
                        "Processing your payment...");
                dialog.show();
                ispayment="yes";
                InsertBookingRecord();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                dialog.dismiss();

                // Payment failed – allow retrying using a different payment method


                //      Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                Log.d("112233", "" + Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage());
                Toast.makeText(activity, "Payment Failed.\nPlease try again later", Toast.LENGTH_SHORT).show();
                }

        }


        @Override

        public void onError(@NonNull Exception e) {

            final CheckoutActivityJava activity = activityRef.get();

            if (activity == null) {

                return;

            }

            // Payment request failed – allow retrying using the same payment method

            Toast.makeText(activity, "Payment Failed.\nPlease try again later", Toast.LENGTH_SHORT).show();

        }

    }

}

