package com.wazabe.bebus.bo;

import java.util.ArrayList;

/**
 * Created by 201601 on 3/6/2015.
 */
public class MyRoute {
    public ArrayList<Route> routes=new ArrayList<>();

    public MyRoute(Route route) {
        this.routes.add(route);
    }
}
