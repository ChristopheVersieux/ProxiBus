package com.wazabe.bebus.bo;

/**
 * Created by versieuxchristophe on 02/05/15.
 */

public class Direction {
    public String direction;
    public String trip_headsign;

    public Direction(String direction, String trip_headsign) {
        this.direction = direction;
        this.trip_headsign = trip_headsign;
    }
}