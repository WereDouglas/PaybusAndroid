package com.vuga.paybus;

/**
 * Created by DOUGLAS on 31/01/2017.
 */
public class Route
{
    private String stop;
    private String id;
    private String distance;
    private String start;
    private String seat;
    private String start_time;
    private String end_time;
    private String name;
    private String cost;
    private String bus;

    private String company;
    private String max_passengers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getMax_passengers() {
        return max_passengers;
    }

    public void setMax_passengers(String max_passengers) {
        this.max_passengers = max_passengers;
    }


    public String getStop ()
    {
        return stop;
    }

    public void setStop (String stop)
    {
        this.stop = stop;
    }

    public String getDistance ()
    {
        return distance;
    }

    public void setDistance (String distance)
    {
        this.distance = distance;
    }

    public String getStart ()
    {
        return start;
    }

    public void setStart (String start)
    {
        this.start = start;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCost ()
    {
        return cost;
    }

    public void setCost (String cost)
    {
        this.cost = cost;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [stop = "+stop+", distance = "+distance+", start = "+start+", name = "+name+", cost = "+cost+"]";
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}