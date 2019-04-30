package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 27/10/17.
 */

public class Payment implements Item {
    public long amount;
    public String description;
    public Date date;
    public String resourceId = null;

    public Payment(long amount, String description, Date date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return description;
    }

    public String getSubtitle() {
        return amount+"$";
    }

    public String getDate(){
        if(date != null) {
            return DateHelper.stylizedDate(date);
        }
        return null;
    }

    public String getResource(){
        return null;
    }
    public String getId(){ return null; }
    public boolean isOwner(){ return false; }
    public boolean isCoach(){ return false; }
    public boolean isComunity(){ return false; }
    public String getContentType(){return null;}
    public boolean isNotification(){return false;}
}


