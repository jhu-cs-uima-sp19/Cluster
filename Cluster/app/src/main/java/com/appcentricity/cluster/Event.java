package com.appcentricity.cluster;

import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Event implements Comparable {

    private String title, description, startTime, endTime, location, creator, docPath;
    private Timestamp startTS;
    private long stars;

    public Event() {
    }

    public Event(String title, String description, com.google.firebase.Timestamp startTime, com.google.firebase.Timestamp endTime, String location, String creator, long stars, String docPath) {
        this.title = title;
        this.description = description;

        DateFormat df = new SimpleDateFormat("MM/dd h:mm a");
        this.startTime = df.format(startTime.toDate());
        this.startTS = startTime;
        this.endTime = df.format(endTime.toDate());
        this.location = location;
        this.creator = creator;
        this.stars = stars;
        this.docPath = docPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getStars() {
        return stars;
    }

    public void star() {
        stars++;
    }

    public void unStar() {
        stars--;
    }

    public String getCreator() {
        return this.creator;
    }

    public String getDocPath() {
        return this.docPath;
    }

    public Timestamp getStartTS() {
        return this.startTS;
    }

    @Override
    public int compareTo(Object o) {
        return this.getStartTS().compareTo(((Event)o).getStartTS());
    }
    protected void setStars(long newStar){this.stars = newStar;}
}
