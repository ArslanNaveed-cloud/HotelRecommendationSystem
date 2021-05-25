package project.application.hotelbookingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class roomrvadaphter extends RecyclerView.Adapter<roomrvadaphter.roomrvviewholder>implements Filterable {
    private ArrayList<RoomDataModel> arraylist;
    private ArrayList<RoomDataModel>  arrayListFull;
    private ArrayList<RoomRatingDataModel> modelArrayList;

    private Context context;

    public roomrvadaphter(ArrayList<RoomDataModel> arraylist,ArrayList<RoomRatingDataModel> modelArrayList) {
        this.arraylist = arraylist;
        this.arrayListFull= new ArrayList<>(arraylist);
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public roomrvviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.room_list_layout,parent,false);
        return new roomrvadaphter.roomrvviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull roomrvviewholder holder, final int position) {
        Glide.with(context).load(Urls.DOMAIN+"/assets/roomimages/"+arraylist.get(position).getRoom_coverimage()).
                into(holder.imageView);
        holder.textView.setText(arraylist.get(position).getRoom_name());
        for(int i =0;i<modelArrayList.size();i++){
            if(modelArrayList.get(i).getRoom_id() == arraylist.get(position).getRoom_id()){
                holder.ratingwrapper.setVisibility(View.VISIBLE);
                holder.rating.setText(""+modelArrayList.get(i).getRoom_rating());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,RoomDetail.class);
                intent.putExtra("name",arraylist.get(position).getRoom_name());
                intent.putExtra("hotel_id",arraylist.get(position).getHotel_id());
                intent.putExtra("description",arraylist.get(position).getRoom_description());
                intent.putExtra("coverimage",arraylist.get(position).getRoom_coverimage());
                intent.putExtra("price",arraylist.get(position).getRoom_pric());
                intent.putExtra("discount",arraylist.get(position).getRoom_discount());

                 intent.putExtra("galleryimage",arraylist.get(position).getRoom_galleyimages().toString());
                intent.putExtra("id",arraylist.get(position).getRoom_id());
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

    @Override
    public Filter getFilter() {
        return myfilter;
    }
    private Filter myfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RoomDataModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(RoomDataModel item:arrayListFull){
                    if(item.getRoom_name().toLowerCase().contains(FilterPattern)){
                        filterlist.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterlist;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arraylist.clear();
            arraylist.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public class roomrvviewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,location;
        TextView rating;
        LinearLayout ratingwrapper;
        public roomrvviewholder(View itemView) {
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
