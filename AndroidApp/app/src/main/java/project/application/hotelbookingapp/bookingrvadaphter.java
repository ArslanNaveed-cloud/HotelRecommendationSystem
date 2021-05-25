package project.application.hotelbookingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class bookingrvadaphter extends RecyclerView.Adapter<bookingrvadaphter.bookingrvviewholder>implements Filterable {

    ArrayList<BookingDataModel> arrayList;
    ArrayList<BookingDataModel> arrayListFull;

    Context context;

    public bookingrvadaphter(ArrayList<BookingDataModel> arrayList) {
        this.arrayList = arrayList;
        this.arrayListFull= new ArrayList<>(arrayList);

    }

    @NonNull
    @Override
    public bookingrvviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bookinglist_layout,parent,false);
        return new bookingrvadaphter.bookingrvviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookingrvviewholder holder, int position) {
        Glide.with(context).load(Urls.DOMAIN+"/assets/roomimages/"+arrayList.get(position).getCoveriamge()).
                into(holder.roomcover);
        holder.broomname.setText(arrayList.get(position).getRoomname());
        holder.bhotelname.setText(arrayList.get(position).getHotelname());
        holder.bstartdate.setText("From: "+arrayList.get(position).getStartdate());
        holder.bprice.setText("Paid RS: "+String.valueOf(arrayList.get(position).getRoom_price()));
        holder.bstay.setText("Stay for "+arrayList.get(position).getStay()+" day");
        holder.benddate.setText("To: "+arrayList.get(position).getEnddate());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return myfilter;
    }
    private Filter myfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<BookingDataModel> filterlist = new ArrayList<>();

            if(constraint == null || constraint.length() == 0||constraint.toString().isEmpty()){
                filterlist.addAll(arrayListFull);
            }else{
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for(BookingDataModel item:arrayListFull){
                    if(item.getRoomname().toLowerCase().contains(FilterPattern)){
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
            arrayList.clear();
            arrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };


    public class bookingrvviewholder extends RecyclerView.ViewHolder {
        ImageView roomcover;
        TextView broomname,bhotelname,bstartdate,bstay,bprice,benddate;
        public bookingrvviewholder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            roomcover = itemView.findViewById(R.id.roomcover);
            broomname = itemView.findViewById(R.id.broomname);
            bhotelname = itemView.findViewById(R.id.bhotelname);
            bstartdate = itemView.findViewById(R.id.bstartdate);
            benddate = itemView.findViewById(R.id.benddate);
            bprice = itemView.findViewById(R.id.bprice);
            bstay = itemView.findViewById(R.id.bstay);

        }


    }
}
