package edu.ntu.ssp4_rzdns_outhink.modals;

public class Itinerary {
    public String type;
    public String title;
    public String description;

    public Itinerary(){
        //Default constructor required for calls to datasnapshot.getvalue(user.class)
    }

    public Itinerary(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }
}
