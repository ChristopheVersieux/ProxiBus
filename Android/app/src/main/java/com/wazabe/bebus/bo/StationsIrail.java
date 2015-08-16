package com.wazabe.bebus.bo;

import java.util.ArrayList;

/**
 * Created by 201601 on 3/6/2015.
 */
public class StationsIrail {
    public ArrayList<Stations> stations;
    public class Stations {
        public Station station;
    }
    public class Station {
        public String id;
        public String code;
        public String name;
        public String latitude;
        public String longitude;

    }

}
