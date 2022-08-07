package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.modals.Questionnaire;

public class Question5Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_question5, container, false);

        Bundle bundle = this.getArguments();
        ArrayList<String> prefcat = bundle.getStringArrayList("prefcat");

        Button nextBtn = view.findViewById(R.id.qn5_next);
        Button backBtn = view.findViewById(R.id.qn5_back);
        CheckBox nature = view.findViewById(R.id.checkBox1);
        CheckBox museum = view.findViewById(R.id.checkBox2);
        CheckBox familyfriendly = view.findViewById(R.id.checkBox3);

        int size = prefcat.size();
        if (size == 0)
        {
            nextBtn.setEnabled(false);
        }
        else {
            for (int i = 0; i < size; i++) {
                String labels = prefcat.get(i);
                if (labels.equalsIgnoreCase("NATURE")) {
                    nature.setChecked(true);
                } else if (labels.equalsIgnoreCase("MUSEUM")) {
                    museum.setChecked(true);
                } else if (labels.equalsIgnoreCase("FAMILY-FRIENDLY")) {
                    familyfriendly.setChecked(true);
                }
            }
        }


        nature.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!nature.isChecked())
                {
                    if(!museum.isChecked() && !familyfriendly.isChecked())
                        nextBtn.setEnabled(false);
                }
                else
                    nextBtn.setEnabled(true);
            }
        });

        museum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!museum.isChecked())
                {
                    if(!nature.isChecked() && !familyfriendly.isChecked())
                        nextBtn.setEnabled(false);
                }
                else
                    nextBtn.setEnabled(true);
            }
        });

        familyfriendly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!familyfriendly.isChecked())
                {
                    if(!nature.isChecked() && !museum.isChecked())
                        nextBtn.setEnabled(false);
                }
                else
                    nextBtn.setEnabled(true);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = bundle.getString("useremail");
                String date = bundle.getString("date");
                String endtime = bundle.getString("endtime");
                int numofadults = bundle.getInt("numofadults");
                int numofchildren = bundle.getInt("numofchildren");
                ArrayList<String>prefer = new ArrayList<>();
                if(nature.isChecked())
                {
                    prefer.add("Nature");
                }
                if(museum.isChecked())
                {
                    prefer.add("Museum");
                }
                if(familyfriendly.isChecked())
                {
                    prefer.add("Family-Friendly");
                }
                bundle.putStringArrayList("prefcat",prefer);
                ArrayList<String> prefcat = bundle.getStringArrayList("prefcat");
                String spaddr = bundle.getString("spaddr");
                String starttime = bundle.getString("starttime");
                String longitude = bundle.getString("longitude");
                String latitude = bundle.getString("latitude");

                String id = bundle.getString("id");
                if (!id.equals("")) {

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("questionnaire").child(id);
                    Questionnaire qn = new Questionnaire(date, endtime, numofadults, numofchildren, prefcat, spaddr, starttime, email, longitude, latitude);
                    dbRef.setValue(qn);
                    String successful = "Questionnaire has been updated successfully";
                    Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), SplashCompleteActivity.class);
                    startActivity(intent);
                }
                else{
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("questionnaire");
                    Questionnaire qn= new Questionnaire(date, endtime, numofadults, numofchildren, prefcat, spaddr, starttime, email, longitude, latitude);
                    dbRef.push().setValue(qn);
                    String successful = "Questionnaire has been updated successfully";
                    Toast.makeText(getActivity(), successful, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), SplashCompleteActivity.class);
                    startActivity(intent);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String>prefer = new ArrayList<>();
                if(nature.isChecked())
                {
                    prefer.add("Nature");
                }
                if(museum.isChecked())
                {
                    prefer.add("Museum");
                }
                if(familyfriendly.isChecked())
                {
                    prefer.add("Family-Friendly");
                }
                bundle.putStringArrayList("prefcat",prefer);
                Question4Fragment nextFrag= new Question4Fragment();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }

}
