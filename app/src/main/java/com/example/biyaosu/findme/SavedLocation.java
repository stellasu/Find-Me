package com.example.biyaosu.findme;

/**
 * Created by biyaosu on 5/20/15.
 */
public class SavedLocation {

    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private long updated;
    private int top; //1: true 0:false

    public SavedLocation() {}

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setUpdated(long updated){
        this.updated = updated;
    }

    public long getUpdated(){
        return this.updated;
    }

    public void setTop(int top){
        this.top = top;
    }

    public int getTop(){
        return this.top;
    }


}
