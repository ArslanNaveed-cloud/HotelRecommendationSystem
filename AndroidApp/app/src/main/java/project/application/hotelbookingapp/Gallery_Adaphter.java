package project.application.hotelbookingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;

public class Gallery_Adaphter extends RecyclerView.Adapter<Gallery_Adaphter.gealleryviewhlder> {

    Context context;
    JSONArray galleryimages;
    String identifier;
    public Gallery_Adaphter(JSONArray galleryimages,String identifier) {
        this.galleryimages = galleryimages;
        this.identifier = identifier;
    }

    @NonNull
    @Override
    public gealleryviewhlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.gallery_imageslayout,parent,false);
        return new Gallery_Adaphter.gealleryviewhlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull gealleryviewhlder holder, int position) {
        try {
            final String galleryimage = galleryimages.getString(position);

            if(identifier.equals("room")){
                Glide.with(context).load(Urls.DOMAIN+"/assets/roomimages/"+galleryimage).
                        into(holder.imageView);

            }else {
                Glide.with(context).load(Urls.DOMAIN + "/assets/hotelimages/" + galleryimage).
                        into(holder.imageView);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ShowImage.class);
                    intent.putExtra("FILENAME",galleryimage);
                    intent.putExtra("identifier",identifier);

                    context.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return galleryimages.length();
    }

    public class gealleryviewhlder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,location;

        public gealleryviewhlder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.galleyimage);
            textView = itemView.findViewById(R.id.hotelname);
            location = itemView.findViewById(R.id.hotellocation);

        }
    }
}
