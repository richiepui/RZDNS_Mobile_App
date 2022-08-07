package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import edu.ntu.ssp4_rzdns_outhink.R;

public class Question2Fragment extends Fragment {
    private TextView tvTimer1, tvTimer2;
    int t1Hour, t1Minute, t2Hour, t2Minute;
    String t1_24hr, t2_24hr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_question2, container, false);

        //timer selection
        tvTimer1 = view.findViewById(R.id.startTimeText);
        tvTimer2 = view.findViewById(R.id.endTimeText);
        Button nextBtn = (Button) view.findViewById(R.id.qn2_next);
        Button backBtn = (Button) view.findViewById(R.id.qn2_back);

        Bundle bundle = this.getArguments();
        t1_24hr = bundle.getString("starttime");
        t2_24hr = bundle.getString("endtime");

        nextBtn.setEnabled(!t1_24hr.equals("") || !t2_24hr.equals(""));

        tvTimer1.setText(t1_24hr);
        tvTimer2.setText(t2_24hr);


        view.findViewById(R.id.show_startIcon).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //initialize the time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                                //initialize hour and minute
                                t1Hour = hourOfDay;
                                t1Minute = minute;
                                //initialize calender
                                Calendar calender = Calendar.getInstance();
                                //Set hour and minute
                                calender.set(0,0,0,t1Hour,t1Minute);
                                //Set selected time on text view
                                //android.text.format.DateFormat.format("hh:mm aa", calender);
                                t1_24hr = (String) android.text.format.DateFormat.format("HH:mm", calender);
                                tvTimer1.setText(t1_24hr);

                                try {
                                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                                    if(t2_24hr!=null) {
                                        Date endTime = formatter.parse(t2_24hr);
                                        Date startTime = formatter.parse(t1_24hr);

                                        if (startTime.compareTo(endTime) > 0) {
                                            Toast.makeText(getContext(), "Start Time is Greater than End time, Please Select Again", Toast.LENGTH_SHORT).show();
                                            nextBtn.setEnabled(false);
                                        }
                                        else if((endTime.getTime() - startTime.getTime()) / 1000 / 60 / 60 < 4){
                                            endTime = new Date(startTime.getTime() + (4 * 1000 * 60 * 60));
                                            Toast.makeText(getContext(), "Automatically adjusted end time for 4 hours minimum", Toast.LENGTH_SHORT).show();
                                            t2_24hr = (String) android.text.format.DateFormat.format("HH:mm", endTime);
                                            tvTimer2.setText(t2_24hr);
                                        } else {
                                            nextBtn.setEnabled(true);
                                            //Toast.makeText(getContext(), "End Time is Greater than Start time", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getContext(), "End Time is Empty, Please Select", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e1){
                                    e1.printStackTrace();
                                }
                            }
                        }, 12,0,false
                );
                //Displayed previous selected time
                timePickerDialog.updateTime(t1Hour,t1Minute);
                //Show dialog
                timePickerDialog.show();
            }
        });

        view.findViewById(R.id.show_endIcon).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //initialize the time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                                //initialize hour and minute
                                t2Hour = hourOfDay;
                                t2Minute = minute;
                                //initialize calender
                                Calendar calender = Calendar.getInstance();
                                //Set hour and minute
                                calender.set(0,0,0,t2Hour,t2Minute);
                                //Set selected time on text view
                                //android.text.format.DateFormat.format("hh:mm aa", calender);
                                t2_24hr = (String) android.text.format.DateFormat.format("HH:mm", calender);
                                tvTimer2.setText(t2_24hr);
                                try {
                                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                                    if(t1_24hr!=null) {
                                        Date endTime = formatter.parse(t2_24hr);
                                        Date startTime = formatter.parse(t1_24hr);

                                        if (endTime.compareTo(startTime) < 0) {
                                            Toast.makeText(getContext(), "Start Time is Greater than End time, Please Select Again", Toast.LENGTH_SHORT).show();
                                            nextBtn.setEnabled(false);
                                        }
                                        else if((endTime.getTime() - startTime.getTime()) / 1000 / 60 / 60 < 4){
                                            Toast.makeText(getContext(), "A minimum of 4 hours is required", Toast.LENGTH_SHORT).show();
                                            nextBtn.setEnabled(false);
                                        } else {
                                            nextBtn.setEnabled(true);
                                            //Toast.makeText(getContext(), "End Time is Greater than Start time", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getContext(), "Start Time is Empty, Please Select", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e1){
                                    e1.printStackTrace();
                                }

                            }
                        }, 12,0,false
                );
                //Displayed previous selected time
                timePickerDialog.updateTime(t2Hour,t2Minute);
                //Show dialog
                timePickerDialog.show();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question3Fragment nextFrag= new Question3Fragment();
                String timer1 = tvTimer1.getText().toString();
                String timer2 = tvTimer2.getText().toString();
                bundle.putString("starttime",timer1);
                bundle.putString("endtime",timer2);
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
                Question1Fragment nextFrag= new Question1Fragment();
                String timer1 = tvTimer1.getText().toString();
                String timer2 = tvTimer2.getText().toString();
                bundle.putString("starttime",timer1);
                bundle.putString("endtime",timer2);
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