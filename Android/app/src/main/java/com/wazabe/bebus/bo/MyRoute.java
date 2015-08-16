package com.wazabe.bebus.bo;

import java.util.ArrayList;

public class MyRoute {
    public ArrayList<Route> routes=new ArrayList<>();

    public MyRoute(Route route) {
        this.routes.add(route);
    }
}
