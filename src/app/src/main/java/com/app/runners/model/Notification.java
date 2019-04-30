package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 19/8/17.
 */

public class Notification implements Item {
    public String title;
    public String body;
    public String resourceId;
    public String contentType;
    public Date date;

    public Notification(String title, String body, Date date, String contentType) {
        this.title = title;
        this.body = body;
        this.resourceId = null;
        this.date = date;
        this.contentType = contentType;
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

    public String getContentType(){ return this.contentType; }
    public boolean isNotification(){ return true; }

    public String getResource(){
        return null;
    }
    public String getId(){ return null; }
    public boolean isOwner(){ return false; }
    public boolean isCoach(){ return false; }
    public boolean isComunity(){ return false; }
}

