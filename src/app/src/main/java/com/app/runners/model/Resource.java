package com.app.runners.model;

/**
 * Created by Fede_CC on 22/09/2018.
 */

public class Resource {

    public String hash = "";
    public String url = "";
    public String type = "";

    public Resource(){
        this.hash = "";
        this.url = "";
        this.type = "";
    }

    public Resource(String newHash, String newUrl, String newType){
        this.hash = newHash;
        this.url = newUrl;
        this.type = newType;
    }

}
