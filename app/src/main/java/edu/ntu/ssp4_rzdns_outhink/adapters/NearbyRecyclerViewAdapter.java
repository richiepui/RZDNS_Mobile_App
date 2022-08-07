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

public class NearbyRecyclerViewAdapter extends RecyclerView.Adapter<NearbyRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "NearbyRecyclerViewAdapter";
    private ArrayList<Attraction> nearby = new ArrayList<>();
    private Context mContext;
    private FragmentManager fm;


    public NearbyRecyclerViewAdapter(ArrayList<Attraction> nearby, Context mContext, FragmentManager fm) {
        this.nearby = nearby;
        this.mContext = mContext;
        this.fm =  fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_more_card_view,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder Called");
        Glide.with(mContext).asBitmap().load(nearby.get(position).photo_url).into(holder.attractionImage);
        holder.locationName.setText(nearby.get(position).att_name);
        holder.locationRating.setText(String.valueOf(nearby.get(position).att_rating));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Attraction attr;
                attr = nearby.get(holder.getBindingAdapterPosition());
                bundle.putParcelable("sel_attr",attr);
                AttractionDetailsFragment nextFrag = new AttractionDetailsFragment();
                nextFrag.setArguments(bundle);
                fm.beginTransaction().replace(R.id.main_explore_fragment, nextFrag,"findThisFragment")
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearby.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView attractionImage;
        TextView locationName;
        TextView locationRating;
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            attractionImage = itemView.findViewById(R.id.card3);
            locationName = itemView.findViewById(R.id.card2);
            locationRating = itemView.findViewById(R.id.card1);
            parentLayout = itemView.findViewById(R.id.parent_layout_card_view);
        }

    }
}
