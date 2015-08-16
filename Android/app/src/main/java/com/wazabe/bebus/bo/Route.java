package com.wazabe.bebus.bo;

import java.util.ArrayList;

public class Route{
    public String route_short_name;
    public String route_id;
    public String stop_id;
    public String route_long_name;
    public String route_color;
    public String route_text_color;
    public ArrayList<Direction> directions;

    public Route(String route_short_name, String route_id, String route_long_name, String route_color, String route_text_color,ArrayList<Direction> directions,String stop_id) {
        this.route_short_name = route_short_name;
        this.route_id = route_id;
        this.stop_id = stop_id;
        this.route_long_name = route_long_name;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.directions=directions;
    }
}
