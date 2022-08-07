package edu.ntu.ssp4_rzdns_outhink.modals;

import java.util.ArrayList;

public class Agenda {
    public String useremail;
    public ArrayList<String> chosenattrs;

    public Agenda(){
        //Default constructor required for calls to datasnapshot.getvalue(user.class)
    }

    public ArrayList<String> getChosenattrs()
    {
        return this.chosenattrs;
    }

    public Agenda(ArrayList<String>chosenattrs, String useremail)
    {
        this.chosenattrs = chosenattrs;
        this.useremail = useremail;
    }

    public int Size()
    {
        return this.chosenattrs.size();
    }
}
