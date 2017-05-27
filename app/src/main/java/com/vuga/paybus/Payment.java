package com.vuga.paybus;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class Payment {

    private String bus;

    private String busID;
    private String barcode;
    private String routeID;
    private String date;
    private String contact;
    private String cost;
    private String id;
    private String userID;
    private String created;
    private String seat;
    private String email;
    private String name;
    private String sync;

    public String getLuggage() {
        return luggage;
    }

    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    private String luggage;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    private String sessionID;
    private String companyID;
    public  Payment(){}

    public  Payment(String barcode,String date, String contact, String cost , String created, String seat, String email,  String name,String sync,String sessionID,String luggage){

        this.busID= busID;
        this.barcode = barcode;
        this.routeID = routeID;
        this.date = date;
        this.contact = contact;
        this.cost = cost;
        this.id = id;
        this.userID = userID;
        this.sessionID = sessionID;
        this.created = created;
        this.seat = seat;
        this.email = email;
        this.name = name;
        this.sync = sync;
        this.luggage = luggage;
        this.companyID = companyID;
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

    public String getBusID ()
    {
        return busID;
    }

    public void setBusID (String busID)
    {
        this.busID = busID;
    }

    public String getBarcode ()
    {
        return barcode;
    }

    public void setBarcode (String barcode)
    {
        this.barcode = barcode;
    }

    public String getRouteID ()
    {
        return routeID;
    }

    public void setRouteID (String routeID)
    {
        this.routeID = routeID;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getContact ()
    {
        return contact;
    }

    public void setContact (String contact)
    {
        this.contact = contact;
    }

    public String getCost ()
    {
        return cost;
    }

    public void setCost (String cost)
    {
        this.cost = cost;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getUserID ()
    {
        return userID;
    }

    public void setUserID (String userID)
    {
        this.userID = userID;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getSeat ()
    {
        return seat;
    }

    public void setSeat (String seat)
    {
        this.seat = seat;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCompanyID ()
    {
        return companyID;
    }

    public void setCompanyID (String companyID)
    {
        this.companyID = companyID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [bus = "+bus+", busID = "+busID+", barcode = "+barcode+", routeID = "+routeID+", date = "+date+", contact = "+contact+", cost = "+cost+", id = "+id+", userID = "+userID+", created = "+created+", seat = "+seat+", email = "+email+", name = "+name+", companyID = "+companyID+"]";
    }
}

