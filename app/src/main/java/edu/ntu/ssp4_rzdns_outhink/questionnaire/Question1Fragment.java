package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import edu.ntu.ssp4_rzdns_outhink.R;

public class Question1Fragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    private TextView dateText;
    private String Date;
    private Button nextBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        //String date = year + "/" + month + "/" + dayOfMonth;
        String date = year + "/" + String.format("%02d",month) + "/" + String.format("%02d", dayOfMonth);
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        LocalDate selectedDate = LocalDate.of(year, month, dayOfMonth);
        if (selectedDate.isBefore(currentDate)) {
            Toast.makeText(getContext(), "["+selectedDate+"] is in the past", Toast.LENGTH_SHORT).show();
            nextBtn.setEnabled(false);
        } else {
            dateText.setText(date);
            nextBtn.setEnabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_question1, container, false);

        nextBtn = (Button) view.findViewById(R.id.qn1_next);

        //calender
        Bundle bundle = this.getArguments();
        String date = bundle.getString("date");

        nextBtn.setEnabled(!date.equals(""));

        dateText = view.findViewById(R.id.editTextDate);
        dateText.setText(date);

        view.findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
                nextBtn.setEnabled(true);
            }
        });

        //nxt button
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question2Fragment nextFrag = new Question2Fragment();
                String sdate = dateText.getText().toString();
                bundle.putString("date",sdate);
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