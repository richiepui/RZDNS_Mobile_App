package edu.ntu.ssp4_rzdns_outhink.questionnaire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.ntu.ssp4_rzdns_outhink.activites.MainActivity;
import edu.ntu.ssp4_rzdns_outhink.R;

public class SplashCompleteActivity extends AppCompatActivity {
    Thread handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_complete);

        handler = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    Intent main = new Intent(SplashCompleteActivity.this, MainActivity.class);
                    startActivity(main);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        handler.start();

//        int currentOrientation = getResources().getConfiguration().orientation;
//        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }
}