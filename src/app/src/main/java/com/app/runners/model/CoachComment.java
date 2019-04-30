package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class CoachComment implements Item {
    public String _id;
    public String title;
    public String body;
    public String resourceId;
    public Date date;

    public CoachComment(String id, String title, String body, String resourceId, Date date) {
        this._id = id;
        this.title = title;
        this.body = body;
        this.resourceId = resourceId;
        this.date = date;
    }

    public CoachComment(String body) {
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

    public Date getDateInt(){
        return this.date;
    }

    public String getResource() {
        return resourceId;
    }

    public void setResource(String resource){
        this.resourceId = resource;
    }

    public String getId(){ return this._id; }
    public boolean isOwner(){ return false; }
    public boolean isCoach(){ return true; }
    public boolean isComunity() { return false; }
    public String getContentType(){return null;}
    public boolean isNotification(){return false;}
}

