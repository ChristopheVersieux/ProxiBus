package com.wazabe.bebus.bo;

public class ATrip {
    public ATrip(String trip_headsign, String departure_time) {
        this.trip_headsign = trip_headsign;
        this.departure_time = departure_time;
    }
    public String departure_time;
    public String trip_headsign;
}
