package com.example.cluster;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Event {

    private String title, description, startTime, endTime, location, orgId, docPath;
    private int stars;

    public Event() {
    }

    public Event(String title, String description, com.google.firebase.Timestamp startTime, com.google.firebase.Timestamp endTime, String location, String orgId, int stars, String docPath) {
        this.title = title;
        this.description = description;

        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        this.startTime = df.format(startTime.toDate());
        this.endTime = df.format(endTime.toDate());
        this.location = location;
        this.orgId = orgId;
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

    public int getStars() {
        return stars;
    }

    public void star() {
        stars++;
    }

    public void unStar() {
        stars--;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public String getDocPath() {
        return this.docPath;
    }
}
