package edu.ntu.ssp4_rzdns_outhink.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.fragments.AttractionDetailsFragment;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;
import edu.ntu.ssp4_rzdns_outhink.modals.Itinerary;

public class ItineraryRecyclerViewAdapter extends RecyclerView.Adapter<ItineraryRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "ItineraryRecyclerViewAdapter";
    private ArrayList<Itinerary> itineraryList = new ArrayList<>();
    private Context mContext;
    private FragmentManager fm;


    public ItineraryRecyclerViewAdapter(ArrayList<Itinerary> itineraryList, Context mContext, FragmentManager fm) {
        this.itineraryList = itineraryList;
        this.mContext = mContext;
        this.fm =  fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itinerary_card,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder Called");
        holder.title.setText(String.valueOf(itineraryList.get(position).title));
        holder.description.setText(String.valueOf(itineraryList.get(position).description));
        if(itineraryList.get(position).type.trim().equals("TRAVEL"))
            holder.img.setImageResource(R.drawable.ic_taxi);
        else if(itineraryList.get(position).type.trim().equals("BUS"))
            holder.img.setImageResource(R.drawable.ic_fa_bus);
        else if(itineraryList.get(position).type.trim().equals("SUBWAY"))
            holder.img.setImageResource(R.drawable.ic_fa_train);
        else if(itineraryList.get(position).type.trim().equals("WALKING"))
            holder.img.setImageResource(R.drawable.ic_fa_person_walking);
        else if(itineraryList.get(position).type.trim().equals("START"))
            holder.img.setImageResource(R.drawable.ic_fa_flag);
        else if(itineraryList.get(position).type.trim().equals("END"))
            holder.img.setImageResource(R.drawable.ic_fa_flag_checkered);
        else
        holder.img.setImageResource(R.drawable.ic_map_pin);
    }

    @Override
    public int getItemCount() {
        return itineraryList == null ? 0 : itineraryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView title;
        TextView description;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            img = itemView.findViewById(R.id.view_itinerary_step);
            title = itemView.findViewById(R.id.view_itinerary_title);
            description = itemView.findViewById(R.id.view_itinerary_description);
        }

    }
}
