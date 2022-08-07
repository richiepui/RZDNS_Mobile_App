package edu.ntu.ssp4_rzdns_outhink.fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.ntu.ssp4_rzdns_outhink.adapters.ExploreFragmentSectionAdapter;
import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.modals.Agenda;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;
import edu.ntu.ssp4_rzdns_outhink.modals.Questionnaire;


public class ExploreFragment extends Fragment {

    private ArrayList<String> prefcat_user = new ArrayList<>(3);
    private String date;
    private String start_time;
    private String end_time;
    private String latitude;
    private String longitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Declare Tab Layout Variables
        Bundle bundle = new Bundle();
        Bundle bundle1 = new Bundle();
        Bundle bundle2 = new Bundle();

        ArrayList<Attraction> listofShopOpenDay = new ArrayList<>();
        ArrayList<Attraction> listOfrating = new ArrayList<>();
        ArrayList<Attraction> listOfdistance = new ArrayList<>();
        TabLayout tablayout;
        ViewPager viewPager;
        View view = inflater.inflate(R.layout.activity_explore2, container, false);
        tablayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);

        //To get User Email
        SharedPreferences prefs = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        Query query = FirebaseDatabase.getInstance().getReference().child("questionnaire")
                .orderByChild("useremail")
                .equalTo(userEmail);
        tablayout.setupWithViewPager(viewPager);
        ExploreFragmentSectionAdapter efsAdapter =
                new ExploreFragmentSectionAdapter(getChildFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> attrChildren = snapshot.getChildren();
                for (DataSnapshot attr : attrChildren) {
                    Questionnaire question = attr.getValue(Questionnaire.class);
                    prefcat_user = question.prefcat;
                    start_time = question.starttime;
                    end_time = question.endtime;
                    date = question.date;
                    latitude = question.latitude;
                    longitude = question.longitude;;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("attractions");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> attrChildren = snapshot.getChildren();
                final ArrayList<Attraction> listofattr = new ArrayList<>();
                for (DataSnapshot attr : attrChildren) {
                    Attraction att = attr.getValue(Attraction.class);
                    att.setId(attr.getKey());
                    listofattr.add(att);
                }
                //Create User Pref Cat List
                // Filter Based on the Category First
                ArrayList<Attraction> listofPrefCat = new ArrayList<>();
                for (int n = 0; n < listofattr.size(); n++) {
                    for (String cat : prefcat_user) {
                        if (listofattr.get(n).att_tags.equalsIgnoreCase(cat))
                            listofPrefCat.add(listofattr.get(n));
                    }
                }
                Collections.shuffle(listofPrefCat);

                //(2) Check the Day and Time user has selected
                date = date.replaceAll("/", "-");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate userDate = LocalDate.parse(date, formatter);
                LocalTime user_Stime = LocalTime.parse(start_time);
                LocalTime user_Etime = LocalTime.parse(end_time);
                String dayofWeek = userDate.getDayOfWeek().toString().substring(0, 3).toLowerCase();
                String checkDayOpen = dayofWeek + "_open";
                String checkDayClose = dayofWeek + "_close";

                //Enhanced Code to Reduce a For Loop From Previous Code
                for (int n = 0; n < listofPrefCat.size(); n++) {
                    if (!listofPrefCat.get(n).att_op_hr.get(checkDayOpen).equalsIgnoreCase("close")
                            && !listofPrefCat.get(n).att_op_hr.get(checkDayClose).equalsIgnoreCase("close")) {
                        LocalTime shop_Stime = LocalTime.parse(listofPrefCat.get(n).att_op_hr.get(checkDayOpen));
                        LocalTime shop_Etime = LocalTime.parse(listofPrefCat.get(n).att_op_hr.get(checkDayClose));
                        if ((user_Stime.isAfter(shop_Stime) || user_Stime.equals(shop_Stime)) && (user_Etime.isBefore(shop_Etime)
                                || user_Etime.equals(shop_Etime))) {
                            listofShopOpenDay.add(listofPrefCat.get(n));
                        }
                    }
                }

                //To Sort The Attractions
                ArrayList<Attraction> sortListOfRating = new ArrayList<>(listofShopOpenDay);
                revInsertionSort(sortListOfRating);
                listOfrating.addAll(sortListOfRating);

                //For the distance (nearby you)
                double NTU_lat = Double.parseDouble(latitude);
                double NTU_long = Double.parseDouble(longitude);
                ArrayList<Attraction> listofzerodist = new ArrayList<>(listofShopOpenDay);
                for (int i = 0; i < listofzerodist.size(); i++) {
                    listofzerodist.get(i).distance = distance(NTU_lat, NTU_long,
                            listofzerodist.get(i).att_lat, listofzerodist.get(i).att_lng);
                }

                //Sort them based on their distance
                InsertionSortDistance(listofzerodist);
                listOfdistance.addAll(listofzerodist);

                bundle.putParcelableArrayList("ForYou", listofShopOpenDay);
                bundle1.putParcelableArrayList("MostPopular", listOfrating);
                bundle2.putParcelableArrayList("NearbyYou", listOfdistance);
                efsAdapter.addFragment(new ForYouFragment(), "For You", bundle);
                efsAdapter.addFragment(new MostPopularFragment(), "Most Popular", bundle1);
                efsAdapter.addFragment(new NearbyFragment(), "Nearby You", bundle2);
                viewPager.setAdapter(efsAdapter);
                //Move attractions that are already added to agenda to the rear of the list
                sortAddedAttractions(listofShopOpenDay, listOfrating, listOfdistance, userEmail, efsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    public void revInsertionSort(ArrayList<Attraction> a1) {
        for (int i = 0; i < a1.size() - 1; i++) {
            int m = i;
            for (int j = i + 1; j < a1.size(); j++) {
                if (a1.get(m).att_rating < a1.get(j).att_rating)
                    m = j;
            }
            //swapping elements at position i and m
            Attraction temp = a1.get(i);
            a1.set(i, a1.get(m));
            a1.set(m, temp);
        }
    }

    public void InsertionSortDistance(ArrayList<Attraction> a2) {
        for (int i = 0; i < a2.size() - 1; i++) {
            int m = i;
            for (int j = i + 1; j < a2.size(); j++) {
                if (a2.get(m).distance > a2.get(j).distance)
                    m = j;
            }
            //swapping elements at position i and m
            Attraction temp = a2.get(i);
            a2.set(i, a2.get(m));
            a2.set(m, temp);
        }
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // in miles, change to 6371 for kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist;
    }


    private void sortAddedAttractions(ArrayList<Attraction> listofShopOpenDay, ArrayList<Attraction> listOfrating, ArrayList<Attraction> listOfdistance, String userEmail, ExploreFragmentSectionAdapter efsAdapter) {
        //Push attractions that are already in agenda to the end of the page
        Query getAgendasQuery = FirebaseDatabase.getInstance().getReference().child("agenda")
                .orderByChild("useremail")
                .equalTo(userEmail.replace(".", ","));
        getAgendasQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;
                if (!snapshot.hasChildren())
                    return;
                Agenda att = snapshot.getChildren().iterator().next().getValue(Agenda.class);
                if (att == null || att.getChosenattrs() == null)
                    return;

                //Get a list of attractions in listofShopOpenDay that is already added to Agenda
                List<Attraction> attr = listofShopOpenDay.stream()
                        .filter(i -> att.getChosenattrs().stream().anyMatch(c -> c.equals(i.id)))
                        .collect(Collectors.toList());
                //Remove all the attractions from listofShopOpenDay that is in Agenda
                listofShopOpenDay.removeAll(attr);
                //Add back, it should now be at the rear of the list
                listofShopOpenDay.addAll(attr);


                //Same for rating tab
                attr = listOfrating.stream()
                        .filter(i -> att.getChosenattrs().stream().anyMatch(c -> c.equals(i.id)))
                        .collect(Collectors.toList());
                listOfrating.removeAll(attr);
                listOfrating.addAll(attr);


                //Same for distance tab
                attr = listOfdistance.stream()
                        .filter(i -> att.getChosenattrs().stream().anyMatch(c -> c.equals(i.id)))
                        .collect(Collectors.toList());
                listOfdistance.removeAll(attr);
                listOfdistance.addAll(attr);

                //Go through the adapter fragments and call notifyDataSetChanged()
                //on all RecyclerView adapters to invoke invalidations
                for (int i = 0; i < efsAdapter.getCount(); i++) {
                    Fragment tmp = efsAdapter.getItem(i);
                    if (tmp.getView() != null) {
                        RecyclerView target = null;
                        target = tmp.getView().findViewById(R.id.for_you_recycler_view);
                        if (target == null)
                            target = tmp.getView().findViewById(R.id.most_popular_recycler_view);
                        if (target == null)
                            target = tmp.getView().findViewById(R.id.nearby_you_recycler_view);
                        target.getAdapter().notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}