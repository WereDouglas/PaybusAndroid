package com.vuga.paybus;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class Session {
    private String bus;
    private String sessionID;
    private String date;
    private String route;
    private String status;
    private String seats;
    private String sync;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    private String cost;

    public Session(){}

    public Session(String sessionID, String date, String route,  String seats, String status, String sync,String bus,String cost){

        this.sessionID= sessionID;
        this.date = date;
        this.route = route;
        this.status = status;
        this.seats = seats;
        this.sync = sync;
        this.bus = bus;
        this.cost = cost;
    }
    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }
    public String getBus ()
    {
        return bus;
    }

    public void setBus (String bus)
    {
        this.bus = bus;
    }

    public String getSessionID ()
    {
        return sessionID;
    }

    public void setSessionID (String sessionID)
    {
        this.sessionID = sessionID;
    }
    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getRoute ()
    {
        return route;
    }
    public void setRoute (String route)
    {
        this.route = route;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getSeat ()
    {
        return seats;
    }

    public void setSeat (String seats)
    {
        this.seats = seats;
    }

}

