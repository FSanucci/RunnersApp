package com.app.runners.model;

import android.graphics.Bitmap;

import com.app.runners.interfaces.Item;
import com.app.runners.interfaces.Item;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 20/8/17.
 */

public class Comment implements Item {
    public String _id;
    public String autor;
    public String title;
    public boolean owner;
    public boolean coach;
    public boolean comunity;
    public String body;
    public String resourceId = null;
    public Date date;

    public Comment(String id, String title, String body, String resourceId, Date date) {
        this._id = id;
        this.title = title;
        this.body = body;
        this.resourceId = resourceId;
        this.date = date;
        this.owner = false;
        this.coach = false;
        this.comunity = false;
    }

    public Comment (String id, boolean owner, boolean coach, String title, String body, String resourceId, Date date){
        this._id = id;
        this.owner = owner;
        this.coach = coach;
        this.title = title;
        this.body = body;
        this.resourceId = resourceId;
        this.date = date;
        this.comunity = true;
    }

    public Comment(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return body;
    }

    public void setAutor(String autor){
        this.autor = autor;
    }

    public String getAutor(){
        return this.autor;
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

    public void setTitle(String title){
        this.title = title;
    }

    public void setResource(String newResource){
        this.resourceId = newResource;
    }
    public String getResource(){ return this.resourceId; }
    public String getId(){ return this._id; }
    public boolean isOwner(){ return this.owner; }
    public boolean isCoach(){ return this.coach; }
    public boolean isComunity(){ return this.comunity; }
    public String getContentType(){return null;}
    public boolean isNotification(){return false;}

}