package com.wazabe.bebus.bo;

public class BusStop implements Comparable<BusStop> {
    public String stop_name;
    public String stop_id;
    public float stop_lat;
    public float stop_lon;
    public float distance;

    public BusStop(String name,String id, float lat, float lng, float distance) {
        this.stop_name = name;
        this.stop_id = id;
        this.stop_lat = lat;
        this.stop_lon = lng;
        this.distance = distance;
    }

    @Override
    public int compareTo(BusStop o) {
        if(this.stop_name.contentEquals(o.stop_name))
            return 0;
        return  Float.compare(this.distance,o.distance);
    }
}
