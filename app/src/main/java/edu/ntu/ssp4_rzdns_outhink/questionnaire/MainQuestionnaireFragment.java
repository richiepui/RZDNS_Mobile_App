package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.modals.Questionnaire;

public class MainQuestionnaireFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_questionnaire, container, false);

        Bundle bundle= this.getArguments();
        String email =bundle.getString("useremail");
        String encoded = EncodeString(email);

        Button nextBtn = (Button) view.findViewById(R.id.edit);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question1Fragment nextFrag= new Question1Fragment();
                Query reff = FirebaseDatabase.getInstance().getReference().child("questionnaire")
                        .orderByChild("useremail")
                        .equalTo(encoded);

                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            Iterable<DataSnapshot> attrChildren = dataSnapshot.getChildren();

                            for(DataSnapshot attr : attrChildren) {
                                Questionnaire att = attr.getValue(Questionnaire.class);
                                bundle.putString("id", attr.getKey());
                                bundle.putString("date",att.date);
                                bundle.putString("endtime",att.endtime);
                                bundle.putInt("numofadults",att.numofadults);
                                bundle.putInt("numofchildren",att.numofchildren);
                                bundle.putStringArrayList("prefcat",att.prefcat);
                                bundle.putString("spaddr",att.spaddr);
                                bundle.putString("starttime",att.starttime);
                                bundle.putString("useremail",att.useremail);

                            }
                            nextFrag.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        return view;
    }
    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }


}