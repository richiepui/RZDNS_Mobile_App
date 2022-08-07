package edu.ntu.ssp4_rzdns_outhink.modals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItineraryRoute {
    public JSONObject json;
    public Attraction attraction;
    public boolean isJson = true;

    public ItineraryRoute(){
        isJson = false;
    }

    public ItineraryRoute(String routeJson) throws JSONException {
        this.json = new JSONObject(routeJson);
    }
    public ItineraryRoute(String routeJson, Attraction attraction) throws JSONException {
        this.json = new JSONObject(routeJson);
        this.attraction = attraction;
    }

    public String getDistance(){
        try {
            return getLeg().getJSONObject("distance").getString("text");
        } catch (JSONException e) {
            return "";
        }
    }
    public int getDistanceMetres(){
        try {
            return Integer.valueOf(getLeg().getJSONObject("distance").getString("value"));
        } catch (JSONException e) {
            return 0;
        }
    }
    public String getDuration(){
        try {
            return getLeg().getJSONObject("duration").getString("text");
        } catch (JSONException e) {
            return "";
        }
    }
    public int getDurationSeconds(){
        try {
            return Integer.valueOf(getLeg().getJSONObject("duration").getString("value"));
        } catch (JSONException e) {
            return 0;
        }
    }

    public String getStartLocation(){
        try {
            return getLeg().getString("start_address");
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public String getEndLocation(){
        try {
            return getLeg().getString("end_address");
        } catch (JSONException e) {
            return "Unavailable";
        }
    }

    public JSONArray getSteps(){
        try {
            return getLeg().getJSONArray("steps");
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONObject getLeg(){
        try {
            return json.getJSONArray("legs").getJSONObject(0);
        } catch (JSONException e) {
            return null;
        }
    }
}
