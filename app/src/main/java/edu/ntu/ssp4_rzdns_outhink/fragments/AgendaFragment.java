package edu.ntu.ssp4_rzdns_outhink.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.adapters.AgendaRecyclerViewAdapter;
import edu.ntu.ssp4_rzdns_outhink.modals.Agenda;

public class AgendaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_agenda_recyclerview, container, false);
        Bundle bundle = this.getArguments();
        String email = bundle.getString("useremail");
        String encoded = EncodeString(email);

        Query reff = FirebaseDatabase.getInstance().getReference().child("agenda")
                .orderByChild("useremail")
                .equalTo(encoded);

        ArrayList<String> image = new ArrayList<>();
        ArrayList<String> locationName = new ArrayList<>();
        ArrayList<String> locationDescription = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        AgendaRecyclerViewAdapter adapter = new AgendaRecyclerViewAdapter(image,locationName,locationDescription,this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot agd : dataSnapshot.getChildren()) {
                        // get the ID of chosen attractions of the user
                        Iterable<DataSnapshot> agdChildren = dataSnapshot.getChildren();
                        final ArrayList<String> ids = new ArrayList<>();
                        final ArrayList<ArrayList<String>> listofagd = new ArrayList<>();
                        for (DataSnapshot attr : agdChildren) {
                            Agenda att = attr.getValue(Agenda.class);
                            ids.add(attr.getKey());
                            if (att.chosenattrs != null) {
                                listofagd.add(att.chosenattrs);
                            }
                        }

                        // call to attractions json
                        DatabaseReference attrref = FirebaseDatabase.getInstance().getReference().child("attractions");
                        if (listofagd.size()> 0){
                            for(int a = 1; a<= listofagd.get(0).size(); a++){
                                attrref.child(String.valueOf(listofagd.get(0).get(a-1))).addValueEventListener(new ValueEventListener(){

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("att_name").getValue(String.class);
                                        String address = snapshot.child("att_address").getValue(String.class);
                                        String photo = snapshot.child("photo_url").getValue(String.class);
                                        image.add(photo);
                                        locationName.add(name);
                                        locationDescription.add(address);
                                        adapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }


}