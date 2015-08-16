package com.wazabe.bebus.bo;

import java.util.ArrayList;

public class Routes {
    public Aroute route;

    public class Aroute {
        public ArrayList<Stop> stops;

        public class Stop {
            public String stop_name;
            public float stop_lat;
            public float stop_lon;
        }
    }

}
