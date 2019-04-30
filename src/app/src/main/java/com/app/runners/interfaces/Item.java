package com.app.runners.interfaces;

/**
 * Created by sergiocirasa on 19/8/17.
 */

public interface Item {
    public String getId();
    public String getTitle();
    public String getSubtitle();
    public String getDate();
    public String getResource();
    public boolean isOwner();
    public boolean isCoach();
    public boolean isComunity();
    public String getContentType();
    public boolean isNotification();
}
