package edu.ntu.ssp4_rzdns_outhink.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.ntu.ssp4_rzdns_outhink.R;

public class HomeActivity extends AppCompatActivity {

    Button getstarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getstarted = (Button) findViewById(R.id.btn_getstarted);
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SelectOption.class));
            }
        });

    }
}