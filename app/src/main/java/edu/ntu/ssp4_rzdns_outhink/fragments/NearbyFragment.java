package edu.ntu.ssp4_rzdns_outhink.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.adapters.NearbyRecyclerViewAdapter;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;

public class NearbyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_nearby_recyclerview, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<Attraction> nearbyU = bundle.getParcelableArrayList("NearbyYou");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerView recyclerView = view.findViewById(R.id.nearby_you_recycler_view);
        NearbyRecyclerViewAdapter adapter = new NearbyRecyclerViewAdapter(nearbyU, this.getContext(),fm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),2));
        return view;
    }
}