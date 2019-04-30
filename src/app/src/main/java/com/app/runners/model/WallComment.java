package com.app.runners.model;

import java.util.Date;

/**
 * Created by devcreative on 1/17/18.
 */

public class WallComment {

    public String _id;
    public boolean owner;
    public boolean coach;
    public Date crationDate;
    public String author;
    public String text;

    public WallComment(String id, boolean owner, boolean coach, Date date, String author, String text){
        this._id = id;
        this.owner = owner;
        this.coach = coach;
        this.crationDate = date;
        this.author = author;
        this.text = text;
    }

    public WallComment(){
        this._id = null;
        this.owner = false;
        this.coach = false;
        this.crationDate = new Date();
        this.author = null;
        this.text = null;
    }

}
