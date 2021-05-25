package project.application.hotelbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class reviewadaphter extends RecyclerView.Adapter<reviewadaphter.reviewviewholder> {

    ArrayList<ReviewsDataModel> arrayList;
    Context context;

    public reviewadaphter(ArrayList<ReviewsDataModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public reviewviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_layout,parent,false);
        return new reviewadaphter.reviewviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewviewholder holder, int position) {

        holder.name.setText(arrayList.get(position).getMynames());
        holder.experience.setText(arrayList.get(position).getMyexperience());
        holder.rating.setText(String.valueOf(arrayList.get(position).getMyrating()));


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class reviewviewholder extends RecyclerView.ViewHolder{
        TextView name,experience,rating;

        public reviewviewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            experience = itemView.findViewById(R.id.experience);
            rating = itemView.findViewById(R.id.rating);

        }
    }
}
