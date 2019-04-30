package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 19/8/17.
 */

public class ProfessionalNote implements Item {
    public String title;
    public String body;
    public Date date;
    public String resourceId;

    public ProfessionalNote(String title, String body, Date date) {
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public ProfessionalNote(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return body;
    }


    public String getDate(){
        if(date != null) {
            return DateHelper.stylizedDate(date);
        }
        return null;
    }

    public String getResource(){
        return  null;
    }
    public String getId(){ return null; }
    public boolean isOwner(){ return false; }
    public boolean isCoach(){ return false; }
    public boolean isComunity(){ return false; }
    public String getContentType(){return null;}
    public boolean isNotification(){return false;}
}

