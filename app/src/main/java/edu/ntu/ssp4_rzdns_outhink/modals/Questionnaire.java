package edu.ntu.ssp4_rzdns_outhink.modals;

import java.util.ArrayList;
import java.util.HashMap;

public class Questionnaire {

    public String id;
    public String date;
    public String endtime;
    public int numofadults;
    public int numofchildren;
    public ArrayList<String> prefcat;
    public String spaddr;
    public String starttime;
    public String useremail;
    public String longitude;
    public String latitude;

    public Questionnaire(){
        //Default constructor required for calls to datasnapshot.getvalue(user.class)
    }

    public Questionnaire(String date, String endtime, int numofadults, int numofchildren, ArrayList<String> prefcat,
                         String spaddr, String starttime, String useremail)
    {
        this.date = date;
        this.endtime = endtime;
        this.numofadults = numofadults;
        this.numofchildren = numofchildren;
        this.prefcat = prefcat;
        this.spaddr = spaddr;
        this.starttime = starttime;
        this.useremail = useremail;
    }

    public Questionnaire(String date, String endtime, int numofadults, int numofchildren, ArrayList<String> prefcat,
                         String spaddr, String starttime, String useremail, String longitude, String latitude)
    {
        this.date = date;
        this.endtime = endtime;
        this.numofadults = numofadults;
        this.numofchildren = numofchildren;
        this.prefcat = prefcat;
        this.spaddr = spaddr;
        this.starttime = starttime;
        this.useremail = useremail;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
