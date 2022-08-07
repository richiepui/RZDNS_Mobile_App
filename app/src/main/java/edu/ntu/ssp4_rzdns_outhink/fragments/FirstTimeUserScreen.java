package edu.ntu.ssp4_rzdns_outhink.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.questionnaire.Question1Fragment;


public class FirstTimeUserScreen extends Fragment {

    Button proceed;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_time_user_screen, container, false);

        Bundle bundle = this.getArguments();

        ArrayList<String> prefcat = new ArrayList<>(3);

        proceed = (Button) view.findViewById(R.id.btn_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("id", "");
                bundle.putString("date","");
                bundle.putString("endtime","");
                bundle.putInt("numofadults",1);
                bundle.putInt("numofchildren",0);
                bundle.putString("longitude","");
                bundle.putString("latitude","");
                bundle.putStringArrayList("prefcat",prefcat);
                bundle.putString("spaddr","");
                bundle.putString("starttime","");
                Question1Fragment nextFrag = new Question1Fragment();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        nextFrag).commit();
            }
        });

        return view;

    }
}