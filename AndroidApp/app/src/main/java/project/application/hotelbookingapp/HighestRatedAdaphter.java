package project.application.hotelbookingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HighestRatedAdaphter extends RecyclerView.Adapter<HighestRatedAdaphter.HighestRatedviewholder> {
    private ArrayList<HighestRatedHotelDataModel> arraylist;
    private Context context;
    private ArrayList<HotelRatingDataModel> modelArrayList;

    public HighestRatedAdaphter(ArrayList<HighestRatedHotelDataModel> hotelDataModel,ArrayList<HotelRatingDataModel> modelArrayList) {
         this.modelArrayList = modelArrayList;
        this.arraylist = hotelDataModel;

    }
    @NonNull
    @Override
    public HighestRatedviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.hotelitemlayout,parent,false);
        return new HighestRatedAdaphter.HighestRatedviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighestRatedviewholder holder, int position) {
        holder.ratingwrapper.setVisibility(View.GONE);
        Glide.with(context).load(Urls.DOMAIN+"/assets/hotelimages/"+arraylist.get(position).getHotel_coverimage()).
                into(holder.imageView);
        holder.textView.setText(arraylist.get(position).getHotel_name());
        holder.location.setText(arraylist.get(position).getHotel_address());
        for(int i =0;i<modelArrayList.size();i++){
            if(modelArrayList.get(i).getHotel_id() == arraylist.get(position).getHotel_id()){
                holder.ratingwrapper.setVisibility(View.VISIBLE);
                holder.rating.setText(""+modelArrayList.get(i).getHotel_rating());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,HotelDetail.class);
                intent.putExtra("name",arraylist.get(position).getHotel_name());
                intent.putExtra("address",arraylist.get(position).getHotel_address());
                intent.putExtra("city",arraylist.get(position).getHotel_city());
                intent.putExtra("description",arraylist.get(position).getHotel_description());
                intent.putExtra("coverimage",arraylist.get(position).getHotel_coverimage());
                intent.putExtra("galleryimage",arraylist.get(position).getHotl_galleyimages().toString());
                intent.putExtra("id",arraylist.get(position).getHotel_id());
                if(holder.rating.getText().toString().trim() !=null || !holder.rating.getText().toString().trim().isEmpty()){
                    String rating =  holder.rating.getText().toString().trim();
                    intent.putExtra("rating",rating);

                }
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class HighestRatedviewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,location;
        LinearLayout ratingwrapper;
        TextView rating;
        public HighestRatedviewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.hotelname);
            location = itemView.findViewById(R.id.hotellocation);
            ratingwrapper = itemView.findViewById(R.id.ratingwrapper);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
