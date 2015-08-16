package com.wazabe.bebus.bo;

import java.util.ArrayList;


public class DepartureIrail {
    public ArrayList<StopTimes> stopTimes;
    public class StopTimes {
        public StopTime stopTime;
    }
    public class StopTime {
        public String headsign;
        public String route_id;
        public String color;
        public String text_color;
        public String direction;
        public String iso8601;

        public StopTime(String headsign, String route_id, String route_color, String route_text_color, String direction, String iso8601) {
            this.headsign = headsign;
            this.route_id = route_id;
            this.color = route_color;
            this.text_color = route_text_color;
            this.direction = direction;
            this.iso8601 = iso8601;
        }

    }

}
