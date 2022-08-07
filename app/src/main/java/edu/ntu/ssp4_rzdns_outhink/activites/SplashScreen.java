package edu.ntu.ssp4_rzdns_outhink.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import edu.ntu.ssp4_rzdns_outhink.R;

public class SplashScreen extends Activity {

    Thread handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    Intent main = new Intent(SplashScreen.this, HomeActivity.class);
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