package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.rest.core.ParserUtils;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 19/8/17.
 */

public class Race implements Item {
    public String id;
    public String title;
    public String body;
    public Date date;
    public Date runningDate;
    public Integer km;
    public String resourceId;

    public Integer durationHour;
    public Integer durationMinute;
    public Integer durationSecond;
    public String raceName;

    public Race(String title, String body, Date date) {
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public Race(String title, Integer km, Date runningDate ) {
        this.title = title;
        this.runningDate = runningDate;
        this.km = km;
    }

    public Race(String id, String title, String body, Date date){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public Race(String id, String title, String body, Date date, Integer km, Integer durationHour, Integer durationMinute, String raceName){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        this.runningDate = date;
        this.km = km;
        this.durationHour = durationHour;
        this.durationMinute = durationMinute;
        this.raceName = raceName;
    }

    public Race(String id, String title, String body, Date date, Integer km, Integer durationHour, Integer durationMinute, Integer durationSecond, String raceName){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        this.runningDate = date;
        this.km = km;
        this.durationHour = durationHour;
        this.durationMinute = durationMinute;
        this.durationSecond = durationSecond;
        this.raceName = raceName;
    }

    public Race(String id, String title, String body, Date creationDate, Date runningDate, Integer km, Integer durationHour, Integer durationMinute, Integer durationSecond, String raceName){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = creationDate;
        this.runningDate = runningDate;
        this.km = km;
        this.durationHour = durationHour;
        this.durationMinute = durationMinute;
        this.durationSecond = durationSecond;
        this.raceName = raceName;
    }

    public Race(String title, Integer km, Date runningDate, Integer durationHour, Integer durationMinute ) {
        this.title = title;
        this.km = km;
        this.runningDate = runningDate;
        this.durationHour = durationHour;
        this.durationMinute = durationMinute;
    }

    public Race(String title, Integer km, Date runningDate, Integer durationHour, Integer durationMinute, Integer durationSecond ) {
        this.title = title;
        this.km = km;
        this.runningDate = runningDate;
        this.durationHour = durationHour;
        this.durationMinute = durationMinute;
        this.durationSecond = durationSecond;
    }

    public Race(String id, String title, String body){
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Race(String body) {
        this.body = body;
    }

    public Integer getKm() {
        return km;
    }
    public Date getRunningDate() {
        return runningDate;
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

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public boolean equals(Object obj) {

        if(obj instanceof Race) {
            Race aRace = (Race) obj;
            return (aRace.id.equals(this.id));
        }

        return false;
    }

    public String getResource(){
        return null;
    }
    public boolean isOwner(){ return false; }
    public boolean isCoach(){ return false; }
    public boolean isComunity(){ return false; }
    public String getContentType(){return null;}
    public boolean isNotification(){return false;}
}

