package com.app.runners.model;

import com.app.runners.interfaces.Item;
import com.app.runners.utils.DateHelper;

import java.util.Date;

/**
 * Created by sergiocirasa on 14/9/17.
 */

public class Link  {
    public String title;
    public String description;
    public String text;
    public String url;

    public Link(String title, String url,String text, String desc) {
        this.title = title;
        this.url = url;
        this.text = text;
        this.description = desc;
    }

}
