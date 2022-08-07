package edu.ntu.ssp4_rzdns_outhink.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.modals.Agenda;
import edu.ntu.ssp4_rzdns_outhink.modals.Attraction;

public class AttractionDetailsFragment extends Fragment {
    DatabaseReference secondlvl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String photo_url, attrName, attrAddr, attrRating, attrDesc, attrUrl, att_id;
        HashMap<String,String> attrOphrs,attrRate;
        View view = inflater.inflate(R.layout.activity_attraction_details, container, false);
        SharedPreferences prefs = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String useremail = prefs.getString("email","");

        Query reff = FirebaseDatabase.getInstance().getReference().child("agenda")
                .orderByChild("useremail")
                .equalTo(useremail);

        Bundle bundle = this.getArguments();

        Attraction attr = bundle.getParcelable("sel_attr");
        photo_url = attr.photo_url;
        attrOphrs = attr.att_op_hr;
        attrRate = attr.att_admin_rate;
        attrName = attr.att_name;
        attrAddr = attr.att_address;
        attrRating = String.valueOf(attr.att_rating);
        attrDesc = attr.att_desc;
        attrUrl = attr.att_url;
        att_id = attr.id;

        // DECLARING TEXTVIEWS
        TextView nametv = view.findViewById(R.id.attractiondetails);
        TextView addtv = view.findViewById(R.id.addressid);
        TextView ratingtv = view.findViewById(R.id.rating);
        TextView desctv = view.findViewById(R.id.desc);
        TextView artv = view.findViewById(R.id.adminrate);

        ImageView img = view.findViewById(R.id.img);
        Glide.with(img.getContext()).load(photo_url).into(img);


        // OPENING HRS
        TextView mon = view.findViewById(R.id.wkdy1);
        mon.setText("Monday " + attrOphrs.get("mon_open") + " - " + attrOphrs.get("mon_close"));

        TextView tue = view.findViewById(R.id.wkdy2);
        tue.setText("Tuesday " + attrOphrs.get("tue_open") + " - " + attrOphrs.get("tue_close"));

        TextView wed = view.findViewById(R.id.wkdy3);
        wed.setText("Wednesday " + attrOphrs.get("wed_open") + " - " + attrOphrs.get("wed_close"));

        TextView thu = view.findViewById(R.id.wkdy4);
        thu.setText("Thursday " + attrOphrs.get("thu_open") + " - " + attrOphrs.get("thu_close"));

        TextView fri = view.findViewById(R.id.wkdy5);
        fri.setText("Friday " + attrOphrs.get("fri_open") + " - " + attrOphrs.get("fri_close"));

        TextView sat = view.findViewById(R.id.wkdy6);
        sat.setText("Saturday " + attrOphrs.get("sat_open") + " - " + attrOphrs.get("sat_close"));

        TextView sun = view.findViewById(R.id.wkdy7);
        sun.setText("Sunday " + attrOphrs.get("sun_open") + " - " + attrOphrs.get("sun_close"));


        artv.setText(attrRate.values().toString());
        nametv.setText(attrName);
        addtv.setText(attrAddr);
        ratingtv.setText(attrRating);
        desctv.setText(attrDesc);

        Button agBtn = view.findViewById(R.id.agendaButton);

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    Iterable<DataSnapshot> attrChildren = dataSnapshot.getChildren();

                    for(DataSnapshot attr : attrChildren) {
                        Agenda att = attr.getValue(Agenda.class);
                        bundle.putString("agendaid",attr.getKey());
                        bundle.putStringArrayList("agendaList",att.getChosenattrs());

                        if(att != null && att.getChosenattrs() != null && att.getChosenattrs().stream().anyMatch(i->i.equals(att_id))) {
                            agBtn.setEnabled(false);
                            agBtn.setText("Added");
                            agBtn.setBackground(getContext().getDrawable(R.drawable.btn_color_disabled_190dp));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String finalAtt_id = att_id;
        agBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String agendaid = bundle.getString("agendaid");
                ArrayList<String> agendaList = new ArrayList<>();
                if (bundle.getStringArrayList("agendaList") != null)
                    agendaList = bundle.getStringArrayList("agendaList");
                String attractid = finalAtt_id;
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                        .child("agenda");

                if(agendaid != null)
                {
                    if(!agendaList.contains(attractid) && agendaList.size()<3)
                    {
                        secondlvl = dbRef.child(agendaid);
                        agendaList.add(attractid);
                        Agenda qn= new Agenda(agendaList,useremail);
                        secondlvl.setValue(qn);
                        String successful = "Agenda has been updated successfully";
                        agBtn.setEnabled(false);
                        agBtn.setText("Added");
                        agBtn.setBackground(getContext().getDrawable(R.drawable.btn_color_disabled_190dp));
                        Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                    }
                    else if (agendaList.size()>=3)
                    {
                        String successful = "Agenda has exceeded the agenda limit";
                        Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                    }
                    else if(agendaList.contains(attractid)) {
                        String successful = "Agenda has already been added";
                        Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> agd = new ArrayList<>();
                            if (dataSnapshot.exists())
                            {
                                agd.add(attractid);
                                Agenda agenda = new Agenda(agd, useremail);
                                dbRef.push().setValue(agenda);
                                String successful = "Agenda has been updated successfully";
                                agBtn.setEnabled(false);
                                agBtn.setText("Added");
                                agBtn.setBackground(getContext().getDrawable(R.drawable.btn_color_disabled_190dp));
                                Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

        });

        Button urlBtn = view.findViewById(R.id.urlBtn);
        urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(attrUrl)));
            }
        });

        return view;
    }
}