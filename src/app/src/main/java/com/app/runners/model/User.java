package com.app.runners.model;

import com.app.runners.rest.RestConstants;

import java.util.ArrayList;

/**
 * Created by sergiocirasa on 14/8/17.
 */

public class User {

    public String id;
    public String token;
    public String firebaseToken;
    public String facebookId;
    public String email;
    public String password;
    public String group;
    public String logo;
    public String theme;

    public ArrayList<Race> races;
    public ArrayList<Race> racesWish;
    public Profile profile;
    public HealthInsurance healthInsurance;
    public Documentation documentation;
    public RunningPlan plan;
    public ArrayList<Link> links;

    public ArrayList<Resource> resources;

    public String getLogoPath(){
        if(logo!=null)
            return RestConstants.IMAGE_HOST + logo;
        else return null;
    }
}
