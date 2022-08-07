package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.fragment.app.Fragment;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.ntu.ssp4_rzdns_outhink.R;

public class Question4Fragment extends Fragment {

    private Button nextBtn, backBtn;
    private ImageButton incrementAdult, decrementAdult, incrementChild, decrementChild;
    private int adultdata, childdata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_question4, container, false);

        Bundle bundle = this.getArguments();
        int numofadults = bundle.getInt("numofadults");
        int numofchildren = bundle.getInt("numofchildren");

        nextBtn = view.findViewById(R.id.qn4_next);
        backBtn = view.findViewById(R.id.qn4_back);

        nextBtn.setEnabled(numofadults>0);

        TextView data = view.findViewById(R.id.tv_DataAdult);
        TextView data2 = view.findViewById(R.id.tv_DataChild);

        data.setText(String.valueOf(numofadults));
        data2.setText(String.valueOf(numofchildren));


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNoAdults = Integer.parseInt(data.getText().toString());
                int newNoChilds = Integer.parseInt(data2.getText().toString());
                bundle.putInt("numofadults", newNoAdults);
                bundle.putInt("numofchildren", newNoChilds);
                Question5Fragment nextFrag= new Question5Fragment();
                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question3Fragment nextFrag= new Question3Fragment();
                int newNoAdults = Integer.parseInt(data.getText().toString());
                int newNoChilds = Integer.parseInt(data2.getText().toString());
                bundle.putInt("numofadults", newNoAdults);
                bundle.putInt("numofchildren", newNoChilds);

                nextFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        incrementAdult = view.findViewById(R.id.btn_increaseAdultNo);
        decrementAdult = view.findViewById(R.id.btn_descreaseAdultNo);


        adultdata = Integer.parseInt(data.getText().toString());

        //When loading in data, check if adult data number is either the max number of the min number,
        //If it is either, set the visibility of these image buttons to 0
        if(adultdata == 10)
        {
            incrementAdult.setVisibility(View.INVISIBLE);
            decrementAdult.setVisibility(View.VISIBLE);
        }
        else if (adultdata == 1)
        {
            decrementAdult.setVisibility(View.INVISIBLE);
            incrementAdult.setVisibility(View.VISIBLE);
        }


        incrementAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking after the update whether if it hits the Max Number
                TextView data = view.findViewById(R.id.tv_DataAdult);
                adultdata = Integer.parseInt(data.getText().toString());
                adultdata++;
                data.setText(String.valueOf(adultdata));
                if (adultdata == 10)
                {
                    incrementAdult.setVisibility(View.INVISIBLE);
                    decrementAdult.setVisibility(View.VISIBLE);
                }
                else
                {
                    incrementAdult.setVisibility(View.VISIBLE);
                    decrementAdult.setVisibility(View.VISIBLE);
                }

            }
        });

        decrementAdult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Checking after the update to see whether it has reached the min number
                TextView data = view.findViewById(R.id.tv_DataAdult);
                int adultdata = Integer.parseInt(data.getText().toString());

                adultdata--;
                data.setText(String.valueOf(adultdata));

                if(adultdata == 1)
                {
                    decrementAdult.setVisibility(View.INVISIBLE);
                    incrementAdult.setVisibility(View.VISIBLE);
                }
                else
                {
                    incrementAdult.setVisibility(View.VISIBLE);
                    decrementAdult.setVisibility(View.VISIBLE);
                }
            }
        });

        incrementChild = view.findViewById(R.id.btn_increaseChildNo);
        decrementChild = view.findViewById(R.id.btn_descreaseChildNo);

        childdata = Integer.parseInt(data2.getText().toString());

        //When loading in data, check if adult data number is either the max number of the min number,
        //If it is either, set the visibility of these image buttons to 0
        if(childdata == 10)
        {
            incrementAdult.setVisibility(View.INVISIBLE);
            decrementAdult.setVisibility(View.VISIBLE);
        }
        else if (childdata == 0)
        {
            decrementChild.setVisibility(View.INVISIBLE);
            incrementChild.setVisibility(View.VISIBLE);
        }

        incrementChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView data = view.findViewById(R.id.tv_DataChild);
                int childdata = Integer.parseInt(data.getText().toString());

                incrementChild.setVisibility(View.VISIBLE);
                decrementChild.setVisibility(View.VISIBLE);
                childdata++;
                data.setText(String.valueOf(childdata));
                if (childdata == 10)
                {
                    incrementChild.setVisibility(View.INVISIBLE);
                    decrementChild.setVisibility(View.VISIBLE);
                }
                else
                {
                    incrementChild.setVisibility(View.VISIBLE);
                    decrementChild.setVisibility(View.VISIBLE);
                }
            }
        });

        decrementChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                TextView data = view.findViewById(R.id.tv_DataChild);
                int childdata = Integer.parseInt(data.getText().toString());
                decrementChild.setVisibility(View.VISIBLE);
                incrementChild.setVisibility(View.VISIBLE);
                childdata--;
                data.setText(String.valueOf(childdata));
                if (childdata == 0)
                {
                    decrementChild.setVisibility(View.INVISIBLE);
                    incrementChild.setVisibility(View.VISIBLE);
                }
                else
                {
                    decrementChild.setVisibility(View.VISIBLE);
                    incrementChild.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;

    }

}