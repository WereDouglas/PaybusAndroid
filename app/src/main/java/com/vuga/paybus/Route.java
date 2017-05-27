package com.vuga.paybus;

/**
 * Created by DOUGLAS on 31/01/2017.
 */
public class Route
{
    private String stop;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String distance;
    private String start;
    private String seat;


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

    private String start_time;
    private String end_time;
    private String name;

    private String cost;

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