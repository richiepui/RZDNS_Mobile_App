package edu.ntu.ssp4_rzdns_outhink.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.adapters.ItineraryRecyclerViewAdapter;
import edu.ntu.ssp4_rzdns_outhink.modals.Agenda;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;
import edu.ntu.ssp4_rzdns_outhink.modals.Itinerary;
import edu.ntu.ssp4_rzdns_outhink.modals.ItineraryRoute;
import edu.ntu.ssp4_rzdns_outhink.modals.Questionnaire;
import edu.ntu.ssp4_rzdns_outhink.runnables.DirectionsRequestRunnable;

public class ItineraryFragment extends Fragment {
    private long planTimeSeconds = 0;
    private long plannedTimeSeconds = 0;
    private ArrayList<Itinerary> itineraryList = new ArrayList<Itinerary>();
    String startingLat = "1.350847";
    String startingLng = "103.687251";
    String directionType = "";
    boolean retrievedLatLng = false;
    ItineraryRecyclerViewAdapter itineraryRecyclerAdapter;

    Query generatedItineraries = null;
    ValueEventListener itineraryListener = null;

    Query questionnaireQuery = null;
    ValueEventListener questionnaireQueryListener = null;

    Query getAgendasQuery = null;
    ValueEventListener generateItineraryListener = null;

    Map<Query, ArrayList<ValueEventListener>> attractionQueryList = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_itinerary, container, false);
        TextView inputtime =  view.findViewById(R.id.tv_aim3);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerView recyclerView = view.findViewById(R.id.itinerary_recycler_view);
        itineraryRecyclerAdapter = new ItineraryRecyclerViewAdapter(itineraryList, this.getContext(),fm);
        recyclerView.setAdapter(itineraryRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        //Get the user email
        Bundle bundle = this.getArguments();
        String email = bundle.getString("useremail");
        String encoded = EncodeString(email);

        //Check for previously generated itinerary, show it if exists
        generatedItineraries = FirebaseDatabase.getInstance().getReference().child(getString(R.string.itinerary_firebase_path)).child(encoded);
        itineraryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    itineraryList.clear();
                    itineraryRecyclerAdapter.notifyDataSetChanged();
                    ArrayList<Itinerary> tmp = new Gson().fromJson(String.valueOf(dataSnapshot.getValue()), new TypeToken<ArrayList<Itinerary>>() {}.getType());
                    for (int i = 0; i < tmp.size(); i++) {
                        itineraryList.add(tmp.get(i));
                        itineraryRecyclerAdapter.notifyItemInserted(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        generatedItineraries.addListenerForSingleValueEvent(itineraryListener);

        //Get time allocated from questionnaire for accounting of time
        questionnaireQuery = FirebaseDatabase.getInstance().getReference().child("questionnaire")
                .orderByChild("useremail")
                .equalTo(encoded);
        questionnaireQueryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Iterable<DataSnapshot> userdetails = dataSnapshot.getChildren();
                    for(DataSnapshot userdt: userdetails)
                    {
                        String starttime = userdt.child("starttime").getValue(String.class);
                        String endtime = userdt.child("endtime").getValue(String.class);
                        String lat = userdt.child("latitude").getValue(String.class);
                        String lng = userdt.child("longitude").getValue(String.class);
                        if(lat != null && !lat.trim().isEmpty())
                            startingLat = lat;
                        if(lng != null && !lng.trim().isEmpty())
                            startingLng = lng;
                        retrievedLatLng = true;
                        try{
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                            Date start = dateFormat.parse(starttime);
                            Date end = dateFormat.parse(endtime);

                            long difference = end.getTime() - start.getTime();
                            planTimeSeconds = difference / 1000;
                        }catch (ParseException e){

                        }
                        inputtime.setText(starttime + " to " + endtime );

                        long planHours = planTimeSeconds / 60 / 60;
                        if (planHours < 4){
                            Button btn = (Button) view.findViewById(R.id.generate_itinerary_transit);
                            btn.setEnabled(false);
                            btn = (Button) view.findViewById(R.id.generate_itinerary_taxi);
                            btn.setEnabled(false);
                            String planError = "At least 4 hours is needed to generate a plan. Please change your questionnaire input.";
                            Toast.makeText(getActivity(), planError, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        questionnaireQuery.addListenerForSingleValueEvent(questionnaireQueryListener);

        // Attach click event for taxi
        Button gen = (Button) view.findViewById(R.id.generate_itinerary_taxi);
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionType = "TAXI";
                generateItineraryClick(encoded);
            }
        });
        // Attach click event for transit
        gen = (Button) view.findViewById(R.id.generate_itinerary_transit);
        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionType = "TRANSIT";
                generateItineraryClick(encoded);
            }
        });

        return view;
    }

    private void generateItineraryClick(String encoded){

        //Get list of agendas
        getAgendasQuery = FirebaseDatabase.getInstance().getReference().child("agenda")
                .orderByChild("useremail")
                .equalTo(encoded);

        generateItineraryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;
                if(!dataSnapshot.hasChildren())
                    return;
                ArrayList<Attraction> listAttractions = new ArrayList<Attraction>();
                Agenda att = dataSnapshot.getChildren().iterator().next().getValue(Agenda.class);
                if (att == null || att.getChosenattrs() == null) {
                    //============= Basically code from ExploreFragment START =============
                    ArrayList<Attraction> listofShopOpenDay= new ArrayList<>();
                    final ArrayList<String>[] prefcat_user = new ArrayList[]{new ArrayList<>(3)};
                    final String[] date = new String[1];
                    final String[] start_time = new String[1];
                    final String[] end_time = new String[1];
                    Query query = FirebaseDatabase.getInstance().getReference().child("questionnaire")
                            .orderByChild("useremail")
                            .equalTo(encoded);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Iterable<DataSnapshot> attrChildren = snapshot.getChildren();
                            for(DataSnapshot attr : attrChildren) {
                                Questionnaire question = attr.getValue(Questionnaire.class);
                                prefcat_user[0] = question.prefcat;
                                start_time[0] = question.starttime;
                                end_time[0] = question.endtime;
                                date[0] = question.date;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("attractions");
                    reff.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                for (String cat : prefcat_user[0]) {
                                    if (listofattr.get(n).att_tags.equalsIgnoreCase(cat))
                                        listofPrefCat.add(listofattr.get(n));
                                }
                            }
                            Collections.shuffle(listofPrefCat);

                            //(2) Check the Day and Time user has selected
                            date[0] = date[0].replaceAll("/", "-");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate userDate = LocalDate.parse(date[0], formatter);
                            LocalTime user_Stime = LocalTime.parse(start_time[0]);
                            LocalTime user_Etime = LocalTime.parse(end_time[0]);
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
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                            Date start = null;
                            try {
                                start = dateFormat.parse(start_time[0]);
                                Date end = dateFormat.parse(end_time[0]);

                                long difference = end.getTime() - start.getTime();
                                for (int i = 0; i < (int)(difference/1000/60/60/2.2); i++) {
                                    if(listofShopOpenDay.get(i) != null)
                                        listAttractions.add(listofShopOpenDay.get(i));
                                }

                                //Perform generation of itinerary
                                performItineraryGenerate(listAttractions, encoded);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                    //============= Basically code from ExploreFragment END =============
                    Toast.makeText(getActivity(), getString(R.string.itinerary_no_agendas), Toast.LENGTH_LONG).show();
                }
                else {
                    int totalAttractions = att.getChosenattrs().size();
                    for (String attractionId : att.getChosenattrs()) {
                        // Query data for each attraction in agenda
                        Query attraction = FirebaseDatabase.getInstance().getReference().child("attractions").child(attractionId);
                        ValueEventListener attractionValueListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists())
                                    return;

                                listAttractions.add(dataSnapshot.getValue(Attraction.class));
                                if (totalAttractions == listAttractions.size()) {
                                    // Entered when all attractions from agendas are properly/fully loaded
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Generate Itinerary")
                                            .setMessage("Generating a new itinerary will overwrite your existing itinerary.\n\nAre you sure you want to do this?")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    performItineraryGenerate(listAttractions, encoded);
                                                }
                                            })
                                            .setNegativeButton(R.string.no, null).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        };
                        attraction.addListenerForSingleValueEvent(attractionValueListener);
                        ArrayList<ValueEventListener> list;
                        if (!attractionQueryList.containsKey(attraction))
                            list = new ArrayList<>();
                        else
                            list = attractionQueryList.get(attraction);
                        list.add(attractionValueListener);
                        attractionQueryList.put(attraction, list);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        getAgendasQuery.addListenerForSingleValueEvent(generateItineraryListener);
    }

    public void performItineraryGenerate(ArrayList<Attraction> attractions, String emailEncoded){
        if(attractions == null || attractions.size() == 0)
            return;

        itineraryList.clear();
        itineraryRecyclerAdapter.notifyDataSetChanged();

        ExecutorService execService = Executors.newSingleThreadExecutor();
        plannedTimeSeconds = 0;
        final boolean[] forceGeneration = {true};
        final boolean[] prompted = {false};

        String currentLat = startingLat;
        String currentLong = startingLng;


        // Initialize an arraylist to do a basic sorting of destinations by distance in ascending order
        // For simple distance optimization that doesn't take into account of bidirectional travel
        ArrayList<ItineraryRoute> routesFromStart = new ArrayList<>();
        for (int c = 0; c < attractions.size(); c++) {
            Attraction i = attractions.get(c);
            ArrayList<DirectionsRequestRunnable> callables = new ArrayList<>();
            callables.add(new DirectionsRequestRunnable(getActivity(), startingLat, startingLng, String.valueOf(i.att_lat), String.valueOf(i.att_lng)));
            List<Future<String>> futures = null;
            try {
                futures = execService.invokeAll(callables, 10, TimeUnit.SECONDS);
                String json = futures.get(0).get(3, TimeUnit.SECONDS);
                JSONObject jObj = new JSONObject(json);
                JSONArray routes = jObj.getJSONArray("routes");
                JSONObject route = null;
                if (routes.length() > 0)
                    route = routes.getJSONObject(0);
                else
                    Logger.getLogger(ItineraryFragment.class.toString()).warning(String.format("Unable to retrieve any routes from %s to %s", "(" + currentLat + "," + currentLong + ")", i.att_name + "(" + i.att_lat + "," + i.att_lng + ")"));

                if (route != null && routes.length() > 0) {
                    ItineraryRoute iRoute = new ItineraryRoute(route.toString(), i);
                    routesFromStart.add(iRoute);
                }
            } catch (Exception e) {
                return;
            }
        }
        routesFromStart.sort((o1, o2) -> o1.getDistanceMetres() - o2.getDistanceMetres());

        //Add an empty route with startingLocation that will be used as endingLocation
        routesFromStart.add(new ItineraryRoute());


        //Routes calculation
        for (int c = 0; c < routesFromStart.size(); c++) {
            Attraction i = routesFromStart.get(c).attraction;
            ArrayList<DirectionsRequestRunnable> callables = new ArrayList<>();
            if (i != null) {
                Logger.getLogger(ItineraryFragment.class.toString()).info(String.format("Calculating route from %s to %s", "(" + currentLat + "," + currentLong + ")", i.att_name + "(" + i.att_lat + "," + i.att_lng + ")"));
                callables.add(new DirectionsRequestRunnable(getActivity(), currentLat, currentLong, String.valueOf(i.att_lat), String.valueOf(i.att_lng)));
            }
            else
                callables.add(new DirectionsRequestRunnable(getActivity(),currentLat,currentLong,String.valueOf(startingLat), String.valueOf(startingLng)));

            List<Future<String>> futures = null;
            try {
                futures = execService.invokeAll(callables, 10, TimeUnit.SECONDS);
                String json = futures.get(0).get(3, TimeUnit.SECONDS);
                JSONObject jObj = new JSONObject(json);
                JSONArray routes = jObj.getJSONArray("routes");
                JSONObject route = null;
                if (routes.length() > 0)
                    route = routes.getJSONObject(0);
                else
                    if(i!=null)
                        Logger.getLogger(ItineraryFragment.class.toString()).warning(String.format("Unable to retrieve any routes from %s to %s", "(" + currentLat + "," + currentLong + ")", i.att_name + "(" + i.att_lat + "," + i.att_lng + ")"));
                    else
                        Logger.getLogger(ItineraryFragment.class.toString()).warning(String.format("Unable to retrieve any routes from %s to %s", "(" + currentLat + "," + currentLong + ")", " your original starting position " + "(" + startingLat + "," + startingLng + ")"));

                if (route != null && routes.length() > 0) {
                    ItineraryRoute iRoute = new ItineraryRoute(route.toString());

                    if ((forceGeneration[0] && prompted[0]) || plannedTimeSeconds + iRoute.getDurationSeconds() + (2 * 60 * 60) < planTimeSeconds) {
                        if(c==0){
                            itineraryList.add(new Itinerary("Start from " + iRoute.getStartLocation(), "Coordinates: " + startingLat + ", " + startingLng, "START"));
                            itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);
                        }
                        if(i != null) {
                            if(directionType.equalsIgnoreCase("TRANSIT")){
                                JSONArray steps = iRoute.getSteps();
                                for(int k = 0; k<steps.length();k++){
                                    String mode = "WALKING";
                                    String title = steps.getJSONObject(k).getString("html_instructions");
                                    String description = "Expected travel time: " + steps.getJSONObject(k).getJSONObject("duration").getString("text");
                                    if(!steps.getJSONObject(k).getString("travel_mode").equalsIgnoreCase("WALKING")) {
                                        try {
                                            JSONObject transitDetails = steps.getJSONObject(k).getJSONObject("transit_details");
                                            if (transitDetails != null) {
                                                JSONObject line = transitDetails.getJSONObject("line");
                                                JSONObject vehicle = line.getJSONObject("vehicle");
                                                String vehName = "";
                                                if (vehicle != null)
                                                    vehName = vehicle.getString("name").equalsIgnoreCase("Subway") ? "" : vehicle.getString("name");
                                                mode = vehicle.getString("name").toUpperCase();
                                                int numStops = transitDetails.getInt("num_stops");
                                                String name = "";
                                                if (line != null)
                                                    name = line.getString("name");

                                                JSONObject arrival = transitDetails.getJSONObject("arrival_stop");
                                                String arrival_name = "";
                                                if (arrival != null)
                                                    arrival_name = arrival.getString("name") + (vehicle.getString("name").equalsIgnoreCase("Subway") ? " Stn" : "");
                                                JSONObject departure = transitDetails.getJSONObject("departure_stop");
                                                String departure_name = "";
                                                if (departure != null)
                                                    departure_name = departure.getString("name");
                                                title = vehName + " " + name + " to " + arrival_name;
                                                description = "Alight in " + String.valueOf(numStops) + " stops after boarding at " + departure_name + "\n" + description;
                                            }
                                        } catch (JSONException e) {
                                            Log.e(this.getClass().toString(), e.getMessage());
                                        }
                                    }
                                    if(steps.getJSONObject(k).getString("travel_mode").equalsIgnoreCase("WALKING"))
                                        description += "\nDistance: " + steps.getJSONObject(k).getJSONObject("distance").getString("text");
                                    itineraryList.add(new Itinerary(title,description,mode));
                                    itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);
                                }
                            }
                            else {
                                itineraryList.add(new Itinerary("Taxi to " + i.att_name, "Expected travel time: " + iRoute.getDuration(), "TRAVEL"));
                                itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);
                            }
                            itineraryList.add(new Itinerary(i.att_name, "Explore attraction for 2 hours", "ATTRACTION"));
                            itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);

                            plannedTimeSeconds += iRoute.getDurationSeconds() + (2 * 60 * 60);

                            currentLat = String.valueOf(i.att_lat);
                            currentLong = String.valueOf(i.att_lng);
                        }
                        if(c == routesFromStart.size() -1) {
                            itineraryList.add(new Itinerary("Return to " + iRoute.getEndLocation(), "Expected travel time: " + iRoute.getDuration(), "END"));
                            itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);
                        }
                    } else {
                        if (!forceGeneration[0] && prompted[0]) {
                            itineraryList.add(new Itinerary("Return to " + routesFromStart.get(0).getStartLocation(), "", "END"));
                            itineraryRecyclerAdapter.notifyItemInserted(itineraryList.size() - 1);
                            break;
                        }
                        if (!prompted[0]) {
                            new AlertDialog.Builder(super.getContext())
                                    .setTitle("Too many agendas")
                                    .setMessage("Unfortunately, the amount of time you have allocated does not meet our recommended time required to enjoy the facilities.\n\nDo you want to generate the agendas with an overrun timing anyway?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            throw new RuntimeException();
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            forceGeneration[0] = false;
                                            throw new RuntimeException();
                                        }
                                    }).show();

                            try {
                                Looper.loop();
                            } catch (RuntimeException e2) {
                                prompted[0] = true;
                                c--;
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.itinerary_firebase_path)).child(emailEncoded);
        dbRef.setValue(new Gson().toJson(itineraryList));
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    private void listenerHousekeeping(){
        if (generatedItineraries != null && itineraryListener != null)
            generatedItineraries.removeEventListener(itineraryListener);
        if (questionnaireQueryListener != null && questionnaireQueryListener != null)
            questionnaireQuery.removeEventListener(questionnaireQueryListener);
        if(generateItineraryListener != null && generateItineraryListener != null)
            getAgendasQuery.removeEventListener(generateItineraryListener);
        attractionQueryList.forEach((attractionQuery, listenerArrayList) -> {
            listenerArrayList.forEach((listener) ->{
                attractionQuery.removeEventListener(listener);
            });
        });
        attractionQueryList.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        //listenerHousekeeping();
    }

}
