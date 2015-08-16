package com.wazabe.bebus.bo;

/**
 * Created by 201601 on 5/5/2015.
 */
public class Trip {
    public ATrip trip;

    public Trip(ATrip trip) {
        this.trip = trip;
    }

    public class ATrip {
        public String departure_time;
        public String trip_headsign;

        public ATrip(String departure_time, String trip_headsign) {
            this.departure_time = departure_time;
            this.trip_headsign = trip_headsign;
        }
    }


}
