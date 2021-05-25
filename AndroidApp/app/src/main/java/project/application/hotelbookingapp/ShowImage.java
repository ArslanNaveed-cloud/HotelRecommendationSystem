package project.application.hotelbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bogdwellers.pinchtozoom.view.ImageViewPager;
import com.bumptech.glide.Glide;

public class ShowImage extends AppCompatActivity {

    ImageView imageViewPager;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        imageViewPager = findViewById(R.id.pinchtozoom);
        Intent intent = getIntent();
        String filename = intent.getStringExtra("FILENAME");
        String identifier = intent.getStringExtra("identifier");
        Log.d("FILENAME",""+filename);
        String url;
        if(identifier.equals("room")){
            url  = Urls.DOMAIN+"/assets/roomimages/"+filename;

        }else{
            url  = Urls.DOMAIN+"/assets/hotelimages/"+filename;

        }
        url = url.replace(" ","%20");
        Glide.with(ShowImage.this).load(url).into(imageViewPager);
        imageViewPager.setOnTouchListener(new ImageMatrixTouchHandler(ShowImage.this));
    }
}