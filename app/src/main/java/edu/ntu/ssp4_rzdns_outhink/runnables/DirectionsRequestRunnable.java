package edu.ntu.ssp4_rzdns_outhink.runnables;

import android.app.Activity;

import androidx.fragment.app.FragmentActivity;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.activites.MainActivity;

public class DirectionsRequestRunnable implements Callable<String> {
    WeakReference<Activity> mActivity;
    String _latFrom;
    String _lngFrom;
    String _latTo;
    String _lngTo;
    String _mode = "TRANSIT";


    public DirectionsRequestRunnable(Activity activity, String latFrom, String lngFrom, String latTo, String lngTo) {
        mActivity = new WeakReference<Activity>(activity);
        _latFrom = latFrom;
        _lngFrom = lngFrom;
        _latTo = latTo;
        _lngTo = lngTo;
    }
    public DirectionsRequestRunnable(Activity activity, String latFrom, String lngFrom, String latTo, String lngTo, String mode) {
        this(activity, latFrom, lngFrom, latTo, lngTo);
        _mode = mode;
    }

    @Override
    public String call() throws Exception {
        Activity activity = mActivity.get();
        if (activity != null) {
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/directions/json?origin=" + _latFrom + "," + _lngFrom + "&destination=" + _latTo + "," + _lngTo + "&mode=transit&key=" + mActivity.get().getApplicationContext().getString(R.string.API_KEY_DIRECTIONS))
                    .method("GET", null)
                    .build();
            Call call = new OkHttpClient().newCall(request);
            try {
                Response response = call.execute();
                return response.body().string();
            }
            catch(IOException e){
                Logger.getLogger("GetDirections").severe(e.getMessage());
            }
        }
        return "";
    }
}
