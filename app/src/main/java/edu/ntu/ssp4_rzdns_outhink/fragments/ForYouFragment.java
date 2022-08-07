package edu.ntu.ssp4_rzdns_outhink.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.adapters.ForYouRecyclerViewAdapter;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;
import edu.ntu.ssp4_rzdns_outhink.modals.Questionnaire;

public class ForYouFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_for_you_recyclerview, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<Attraction> listofShopOpenDay = bundle.getParcelableArrayList("ForYou");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerView recyclerView = view.findViewById(R.id.for_you_recycler_view);
        ForYouRecyclerViewAdapter adapter = new ForYouRecyclerViewAdapter(listofShopOpenDay, this.getContext(), fm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),2));
        return view;
    }
}