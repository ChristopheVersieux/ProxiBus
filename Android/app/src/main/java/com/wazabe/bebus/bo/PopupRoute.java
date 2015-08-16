package com.wazabe.bebus.bo;

public class PopupRoute {
    public String route_short_name;
    public String route_id;
    public String stop_id;
    public String route_long_name;
    public String route_color;
    public String route_text_color;
    public String direction;
    public String trip_headsign;

    public PopupRoute(String route_short_name, String route_id, String route_long_name, String route_color, String route_text_color, String direction,String trip_headsign, String stop_id) {
        this.route_short_name = route_short_name;
        this.route_id = route_id;
        this.stop_id = stop_id;
        this.route_long_name = route_long_name;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.direction=direction;
        this.trip_headsign=trip_headsign;
    }
}
