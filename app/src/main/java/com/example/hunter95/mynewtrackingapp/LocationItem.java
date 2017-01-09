package com.example.hunter95.mynewtrackingapp;

import android.content.Context;

/**
 * Created by Hunter95 on 12/12/2016.
 */
public class LocationItem {
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public LocationItem(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean addToDatabase(Context context)
    {
      DatabaseHelper MyDb = DatabaseHelper.getInstance(context);
        return MyDb.insertLocToDatabase(this.name,this.latitude,this.longitude);
    }

    public boolean removeFromDatabase(Context context) {
        DatabaseHelper MyDb = DatabaseHelper.getInstance(context);
        return MyDb.removeLocFromDatabase(this.id);
    }
}
