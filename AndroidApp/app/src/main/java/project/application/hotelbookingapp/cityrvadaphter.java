package project.application.hotelbookingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class cityrvadaphter extends RecyclerView.Adapter<cityrvadaphter.cityrvaviewholder> {

    public Integer[] city_images;
    public String[] city_names;
    boolean isclicked = false;
    Context context;
    public cityrvadaphter(Integer[] city_images, String[] city_names) {
        this.city_images = city_images;
        this.city_names = city_names;
    }

    @NonNull
    @Override
    public cityrvaviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.topbar_layout,parent,false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new cityrvadaphter.cityrvaviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final cityrvaviewholder holder, int position) {
        holder.lineview.setVisibility(View.GONE);
        Glide.with(context).load(city_images[position]).
                circleCrop().
                into(holder.imageView);

        holder.textView.setText(city_names[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   holder.lineview.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                HomeFragment fragment = new HomeFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity =(AppCompatActivity) v.getContext();

                if(city_names[position].equals("NearBy")){

                }else{
                    bundle.putString("cityname",city_names[position]);

                }
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                         fragment).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return city_names.length;
    }

    public class cityrvaviewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        View lineview;
        public cityrvaviewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.cityiamge);
           textView = itemView.findViewById(R.id.cityname);
            lineview = itemView.findViewById(R.id.lineview);
        }
    }
}
