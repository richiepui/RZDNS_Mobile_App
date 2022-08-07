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
import edu.ntu.ssp4_rzdns_outhink.adapters.MostPopularRecyclerViewAdapter;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;


public class MostPopularFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_most_popular_recyclerview, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<Attraction> listOfrating  = bundle.getParcelableArrayList("MostPopular");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerView recyclerView = view.findViewById(R.id.most_popular_recycler_view);
        MostPopularRecyclerViewAdapter adapter = new MostPopularRecyclerViewAdapter(listOfrating, this.getContext(), fm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),2));
        return view;
    }
}
